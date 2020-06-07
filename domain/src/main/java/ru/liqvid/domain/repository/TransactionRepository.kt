package ru.liqvid.domain.repository

import io.reactivex.rxjava3.core.Single
import ru.liqvid.domain.model.Result
import ru.liqvid.domain.model.Transaction
import ru.liqvid.domain.model.User

interface TransactionRepository {
    fun getUserTransactions(user: User): Single<Result<List<Transaction>>>
}