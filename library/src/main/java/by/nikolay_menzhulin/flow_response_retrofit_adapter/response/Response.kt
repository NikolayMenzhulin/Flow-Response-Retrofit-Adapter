package by.nikolay_menzhulin.flow_response_retrofit_adapter.response

/**
 * Абстрактная модель ответа от сервера.
 */
sealed class Response<out T> {

    /**
     * Загрузка данных.
     * Получение данных начинается с этого состояния.
     */
    object Loading : Response<Nothing>()

    /**
     * Успешный ответ от сервера и отсутствие каких-либо данных в нём.
     */
    object Empty : Response<Nothing>()

    /**
     * Успешный ответ от сервера с данными в нём.
     *
     * @param data данные, полученные в ответе
     */
    data class Success<T>(internal val data: T) : Response<T>()

    /**
     * Ошибка загрузки данных.
     *
     * @param error ошибка, полученная при загрузке данных
     */
    data class Error(internal val error: Throwable) : Response<Nothing>()

    /**
     * Выполняется ли запрос в данный момент?
     */
    val isLoading: Boolean
        get() = this is Loading

    /**
     * Выполнился ли запрос успешно?
     */
    val isSuccess: Boolean
        get() = this is Success || this is Empty

    /**
     * Выполнился ли запрос с ошибкой?
     */
    val isError: Boolean
        get() = this is Error

    /**
     * @return данные из ответа от сервера или exception, если запрос выполнился неуспешно, либо данные отсутствуют.
     */
    fun getData(): T =
        try {
            (this as Success).data
        } catch (e: ClassCastException) {
            throw IllegalStateException("Response without data")
        }

    /**
     * @return данные из ответа от сервера или null, если запрос выполнился неуспешно, либо данные отсутствуют.
     */
    fun getDataOrNull(): T? = (this as? Success)?.data

    /**
     * @return ошибка, полученная при загрузке данных или exception, если запрос выполнился успешно и ошибка отсутствует.
     */
    fun getError(): Throwable =
        try {
            (this as Error).error
        } catch (e: ClassCastException) {
            throw IllegalStateException("Response without error")
        }

    /**
     * @return ошибка, полученная при загрузке данных или null, если запрос выполнился успешно и ошибка отсутствует.
     */
    fun getErrorOrNull(): Throwable? = (this as? Error)?.error
}