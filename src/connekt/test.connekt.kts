import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import java.net.ConnectException

val host: String by env

useCase("Wait app launch") {
    for (attempt in 0..10) {
        try {
            GET("$host/actuator/health") then {
                assertThat(
                    jsonPath().readString("status")
                ).isEqualTo("UP")
            }

            println("App is healthy")

            return@useCase
        } catch (e: ConnectException) {
            Thread.sleep(10_000)
        }
    }
}

useCase("create products") {
    val products = listOf(
        "Apple" to 1.2,
        "Banana" to 0.8,
        "Cherry" to 2.5,
        "Date" to 3.0,
        "Elderberry" to 4.0
    )

    for ((name, price) in products) {
        POST("$host/products") {
            contentType("application/json")

            body(
                """
        {
            "name": "$name",
            "price": $price
        }
        """.trimIndent()
            )
        }
    }
}

useCase("create cities") {
    val cities = listOf(
        "Samara",
        "Moscow",
        "Saint-Petersburg",
        "London",
        "New-York",
    )

    for (name in cities) {
        POST("$host/cities") {
            contentType("application/json")

            body(
                """
        {
            "name": "$name"
        }
        """.trimIndent()
            )
        }
    }
}

useCase("create customers") {
    val customers = listOf("Alex", "John", "Peter", "Ilia", "Simon", "Robert", "David")

    for (customer in customers) {
        POST("$host/customers") {
            contentType("application/json")

            body(
                """
        {
            "name": "$customer",
            "email": "$customer@gmail.com"
        }
        """.trimIndent()
            )
        }
    }
}

val cities: List<Long> by GET("$host/cities") then {
    assertThat(code).isEqualTo(200)

    jsonPath().readList("$[*].id", Long::class.java)
}

val products: List<Long> by GET("$host/products") then {
    assertThat(code).isEqualTo(200)

    jsonPath().readList("$.content[*].id", Long::class.java)
}

val customers: List<Long> by GET("$host/customers") then {
    assertThat(code).isEqualTo(200)

    jsonPath().readList("$.content[*].id", Long::class.java)
}

useCase("Do supply") {
    for (cityId in cities) {
        for (productId in products) {
            POST("$host/inventory/supply") {
                contentType("application/json")

                body(
                    """
        {
            "productId": $productId,
            "cityId": ${cityId},
            "amount": ${Random.nextInt(100, 1000)}
        }
        """.trimIndent()
                )
            }
        }
    }
}

useCase("Create Order") {
    // create order
    val orderId by POST("$host/orders") {
        contentType("application/json")

        body(
            """
        {
            "customerId" : ${customers.first()},
            "cityId": ${cities.first()}
        }
    """.trimIndent()
        )
    } then {
        jsonPath().readLong("$.id")
    }

    // add product
    val orderLineId by POST("$host/orders/{orderId}/lines") {
        pathParam("orderId", orderId)

        contentType("application/json")

        body(
            """
                {
                    "productId": ${products.first()},
                    "amount": 1
            }""".trimIndent()
        )
    } then {
        jsonPath().readLong("$.id")
    }

    // add huge amount of items
    POST("$host/orders/{orderId}/lines/{orderLineId}") {
        pathParam("orderId", orderId)
        pathParam("orderLineId", orderLineId)

        contentType("application/json")

        body(
            """
            {
              "amount": 50000
            }
            """.trimIndent()
        )
    } then {
        assertThat(code).isEqualTo(409)
    }

    // add adequate amount
    POST("$host/orders/{orderId}/lines/{orderLineId}") {
        pathParam("orderId", orderId)
        pathParam("orderLineId", orderLineId)

        contentType("application/json")

        body(
            """
            {
              "amount": 5
            }
            """.trimIndent()
        )
    } then {
        assertThat(code).isEqualTo(200)
    }

    // pay order
    POST("$host/orders/{orderId}/pay") {
        pathParam("orderId", orderId)
    }
}
