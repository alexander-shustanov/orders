import org.assertj.core.api.Assertions
import java.io.File

// обяъвление переменных из энвов. значения указываются в файле connekt.env.json
val host: String by env

// простой GET запрос
GET("$host/customers")

// GET запрос с переменной среды
GET("$host/customers/{id}") {
    pathParam("id", 1)
}

// запрос с тестами
GET("$host/customers/{id}") {
    pathParam("id", 1)
} then {
    // тут обрабатываются результаты
    // пишутся ассерты
    Assertions.assertThat(code).isEqualTo(200)
}

val productIds: List<Long> by GET("$host/products") then {
    jsonPath().readList("$.content[*].id", Long::class.java)
}


// запрос с body
POST("$host/orders") {
    header("Content-Type", "application/json")
    body(
        """
        {
            "customerId" : 10,
            "cityId": 1
        }
    """.trimIndent()
    )
}

// запрос с мультипартом.
// его можно было сделать и через Body, но это не удобно. Поэтому вот dsl простенький
PATCH("$host/products/{id}") {
    pathParam("id", 1)

    multipart {
        part(name = "data", contentType = "application/json") {
            body(
                """
                {
                    "price": 100.0
                }
            """.trimIndent()
            )
        }

        file(
            name = "file",
            fileName = "img.png",
            File("/Users/alexander/Desktop/Screenshot 2025-03-10 at 19.06.45.png")
        )
    }
}

// а вот тут мы запускаем разом целый сценарий
// обрати внимание, что мы обращаемся к переменной productIds, которая объявлена выше через by GET ....
useCase {
    // грузим список городов, аналогично тому, как грузили список продуктов
    data class City(val id: Long)

    // но тут переменная в хранилище уже не пишется, потому что мы внутри flow
    val cities: List<City> by GET("$host/cities") then {
        Assertions.assertThat(code).isEqualTo(200) // если не 200, то падаем
        // аналогично парсим что-то
        jsonPath().readList("$", City::class.java)
    }

    for (city in cities) {
        for (productId in productIds) {
            // а вот тут в цикле делаем посты.
            POST("$host/inventory/supply") {
                header("Content-Type", "application/json")
                body(
                    """
        {
            "productId": $productId,
            "cityId": ${city.id},
            "amount": ${Random.nextInt(100, 1000)}
        }
        """.trimIndent()
                )
            }
        }
    }
}
