package ru.liqvid.domain.usecase

import io.reactivex.rxjava3.core.Single
import ru.liqvid.domain.model.Result
import ru.liqvid.domain.model.Transaction

interface GetUserTransactionsUseCase {
    operator fun invoke(): Single<Result<List<Transaction>>>
}