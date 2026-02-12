package deepankumarpn.github.io.eggchef.base

abstract class BaseUseCase<in Params, out Result> {
    abstract operator fun invoke(params: Params): Result
}
