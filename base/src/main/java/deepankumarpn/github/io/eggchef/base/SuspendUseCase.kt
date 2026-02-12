package deepankumarpn.github.io.eggchef.base

abstract class SuspendUseCase<in Params, out Result> {
    abstract suspend operator fun invoke(params: Params): Result
}
