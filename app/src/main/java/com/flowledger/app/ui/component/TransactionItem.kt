package com.flowledger.app.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flowledger.app.data.local.entity.TransactionEntity
import com.flowledger.app.data.local.entity.TransactionSource
import com.flowledger.app.data.local.entity.TransactionStatus
import com.flowledger.app.data.local.entity.TransactionType
import com.flowledger.app.ui.theme.ExpenseRed
import com.flowledger.app.ui.theme.IncomeGreen
import com.flowledger.app.util.CurrencyFormatter
import com.flowledger.app.util.DateUtils

@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Circle,
                contentDescription = null,
                tint = if (transaction.type == TransactionType.INCOME) IncomeGreen else ExpenseRed,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.note ?: if (transaction.type == TransactionType.INCOME) "收入" else "支出",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    if (transaction.status == TransactionStatus.PENDING) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "待确认",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    if (transaction.source == TransactionSource.AUTO_NOTIFICATION) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "自动",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${DateUtils.formatShortDate(transaction.date)} ${DateUtils.getDayOfWeekDisplay(transaction.date)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = (if (transaction.type == TransactionType.EXPENSE) "-" else "+") +
                        CurrencyFormatter.format(transaction.amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (transaction.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
            )
        }
    }
}

@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AmountText(
    type: TransactionType,
    amount: Long,
    modifier: Modifier = Modifier
) {
    val prefix = if (type == TransactionType.EXPENSE) "-" else "+"
    val color = if (type == TransactionType.INCOME) IncomeGreen else ExpenseRed
    Text(
        text = "$prefix${CurrencyFormatter.format(amount)}",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier
    )
}
