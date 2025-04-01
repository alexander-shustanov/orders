import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat

val host: String by env

GET("$host/products")

POST("$host/products") {
    header("Content-Type", "application/json")
    body(
        """
        {
            "name": "Тыква",
            "price": 158.9
        }
        """.trimIndent()
    )
} then {
    assertThat(
        jsonPath().readString("$.name")
    ).isEqualTo("Тыква")

    assertThat(
        jsonPath().read("$.price", Double::class.java)
    ).isEqualTo(158.9)

    assertThat(
        jsonPath().readLong("$.id")
    ).isNotNull()
}

val orderId by POST("http://localhost:8080/orders") {
    header("Content-Type", "application/json")
    body(
        """
        {
            "customerId": 1,
            "cityId": 1
        }
        """.trimIndent()
    )
} then {
    jsonPath().readLong("$.id")
}

POST("http://localhost:8080/orders/{orderId}/lines") {
    header("Content-Type", "application/json")

    pathParam("orderId", orderId)

    body(
        """
        {
            "productId": 5,
            "amount": 1
        }
        """.trimIndent()
    )
}

useCase {
    data class City(val id: Long)
    val cities: List<City> by GET("$host/cities") then {
        assertThat(code).isEqualTo(200)

        jsonPath().readList("$", City::class.java)
    }

    val productIds: List<Long> by GET("$host/products") then {
        jsonPath().readList("$.content[*].id", Long::class.java)
    }

    for (city in cities) {
        for (productId in productIds) {
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
