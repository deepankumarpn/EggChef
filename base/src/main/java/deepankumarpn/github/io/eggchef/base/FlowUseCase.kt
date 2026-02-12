package deepankumarpn.github.io.eggchef.base

import kotlinx.coroutines.flow.Flow

abstract class FlowUseCase<in Params, out Result> {
    abstract operator fun invoke(params: Params): Flow<Result>
}
