package io.aoriani.ecomm.domain

/**
 * Represents a use case in the application.
 *
 * A use case is a specific action or operation that the user can perform.
 * It encapsulates the business logic for that action.
 *
 * @param I The input type for the use case.
 * @param O The output type for the use case.
 */
fun interface UseCase<in I, out O> {
    /**
     * Invokes the use case with the given input.
     *
     * This function is an operator function, allowing the use case to be invoked like a function.
     * For example: `myUseCase(input)`
     *
     * @param input The input data for the use case.
     * @return The output data from the use case.
     * @throws Exception if an error occurs during the execution of the use case.
     */
    suspend operator fun invoke(input: I): O
}