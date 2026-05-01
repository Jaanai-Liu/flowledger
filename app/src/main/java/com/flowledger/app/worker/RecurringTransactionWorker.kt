package com.flowledger.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flowledger.app.data.local.entity.TransactionEntity
import com.flowledger.app.data.local.entity.TransactionSource
import com.flowledger.app.data.local.entity.TransactionStatus
import com.flowledger.app.data.repository.RecurringRuleRepository
import com.flowledger.app.data.repository.TransactionRepository
import com.flowledger.app.util.DateUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

@HiltWorker
class RecurringTransactionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val recurringRuleRepo: RecurringRuleRepository,
    private val transactionRepo: TransactionRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val today = LocalDate.now()
        val todayEpochDay = today.toEpochDay()
        val todayDayOfMonth = today.dayOfMonth
        val todayDayOfWeek = today.dayOfWeek.value

        val rules = recurringRuleRepo.getActiveRulesDueBefore(todayEpochDay)

        rules.forEach { rule ->
            // Check if this rule should fire today
            val shouldFire = when (rule.frequency) {
                com.flowledger.app.data.local.entity.RecurringFrequency.MONTHLY -> {
                    val targetDay = rule.dayOfMonth ?: 1
                    todayDayOfMonth >= targetDay &&
                            (rule.lastGeneratedDate == null ||
                                    DateUtils.fromEpochDay(rule.lastGeneratedDate!!).monthValue != today.monthValue)
                }
                com.flowledger.app.data.local.entity.RecurringFrequency.WEEKLY -> {
                    val targetDay = rule.dayOfWeek ?: 1
                    todayDayOfWeek == targetDay &&
                            (rule.lastGeneratedDate == null ||
                                    DateUtils.fromEpochDay(rule.lastGeneratedDate!!) < todayEpochDay - 6)
                }
                com.flowledger.app.data.local.entity.RecurringFrequency.YEARLY -> {
                    val targetDay = rule.dayOfMonth ?: 1
                    todayDayOfMonth >= targetDay &&
                            today.monthValue == 1 &&
                            (rule.lastGeneratedDate == null ||
                                    DateUtils.fromEpochDay(rule.lastGeneratedDate!!).year != today.year)
                }
            }

            if (shouldFire) {
                val note = resolveTemplate(rule.noteTemplate, today)
                val transaction = TransactionEntity(
                    type = rule.type,
                    amount = rule.amount,
                    categoryId = rule.categoryId,
                    accountId = rule.accountId,
                    paymentMethodId = rule.paymentMethodId,
                    date = todayEpochDay,
                    note = note,
                    source = TransactionSource.AUTO_RECURRING,
                    status = TransactionStatus.PENDING,
                    recurringRuleId = rule.id
                )
                transactionRepo.insert(transaction)
                recurringRuleRepo.updateLastGenerated(rule.id, todayEpochDay)
            }
        }

        return Result.success()
    }

    private fun resolveTemplate(template: String?, today: LocalDate): String? {
        if (template.isNullOrBlank()) return null
        return template
            .replace("{year}", today.year.toString())
            .replace("{month}", today.monthValue.toString().padStart(2, '0'))
            .replace("{day}", today.dayOfMonth.toString().padStart(2, '0'))
    }
}
