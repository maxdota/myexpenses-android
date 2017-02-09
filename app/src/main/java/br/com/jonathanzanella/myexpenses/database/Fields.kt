package br.com.jonathanzanella.myexpenses.database

/**
 * Created by jzanella on 11/10/16.
 */

enum class Fields constructor(val fieldName: String) {
    ID("id"),
    NAME("name"),
    UUID("uuid"),
    USER_UUID("userUuid"),
    SERVER_ID("serverId"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt"),
    AMOUNT("amount"),
    BALANCE("balance"),
    CREDITED("credited"),
    IGNORE_IN_RESUME("ignoreInResume"),
    DATE("date"),
    DUE_DATE("due_date"),
    INIT_DATE("init_date"),
    END_DATE("end_date"),
    INCOME("income"),
    ACCOUNT_TO_PAY_CREDIT_CARD("accountToPayCreditCard"),
    ACCOUNT_TO_PAY_BILLS("accountToPayBills"),
    ACCOUNT_UUID("accountUuid"),
    SOURCE_UUID("sourceUuid"),
    TYPE("type"),
    REMOVED("removed"),
    SYNC("sync");

    override fun toString(): String {
        return fieldName
    }
}