package dev.aoriani.ecomm.domain.usecases

/**
 * Represents a unit of business logic that can be executed asynchronously.
 *
 * @param P The type of input parameters required to execute the use case.
 * @param R The type of result produced by this use case.
 */
interface UseCase<P, R> {
    /**
     * Executes the use case with the given [params].
     *
     * This is a suspending operator function to allow idiomatic invocation using function-call syntax.
     */
    suspend operator fun invoke(params: P): R
}

/**
 * Convenience operator for invoking no-argument use cases.
 *
 * Delegates to [invoke] with [Unit] as the parameter.
 */
suspend operator fun <R> UseCase<Unit, R>.invoke(): R = invoke(Unit)

/**
 * Type alias for a [UseCase] whose result is wrapped in [Result] to convey success or failure.
 */
typealias ResultUseCase<P, R> = UseCase<P, Result<R>>
