import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import java.net.ConnectException
import kotlin.random.Random

val host: String by env

// Preconditions - get necessary data for testing orders
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

useCase("Test Create Order") {
    val orderId by POST("$host/orders") {
        contentType("application/json")
        body("""
        {
            "customerId": ${customers.first()},
            "cityId": ${cities.first()}
        }
        """.trimIndent())
    } then {
        assertThat(code).isEqualTo(200)
        jsonPath().readLong("$.id")
    }

    // Verify order created successfully
    GET("$host/orders/active") {
        queryParam("customerId", customers.first())
    } then {
        assertThat(code).isEqualTo(200)
        assertThat(jsonPath().readLong("$.id")).isEqualTo(orderId)
    }
}

useCase("Test Get Active Order") {
    // Create an order first
    val customerId = customers[1]
    val orderId by POST("$host/orders") {
        contentType("application/json")
        body("""
        {
            "customerId": $customerId,
            "cityId": ${cities.first()}
        }
        """.trimIndent())
    } then {
        jsonPath().readLong("$.id")
    }

    // Test getting active order
    GET("$host/orders/active") {
        queryParam("customerId", customerId)
    } then {
        assertThat(code).isEqualTo(200)
        assertThat(jsonPath().readLong("$.id")).isEqualTo(orderId)
        assertThat(jsonPath().readLong("$.customerId")).isEqualTo(customerId)
    }

    // Test customer with no active order
    val nonExistingCustomerId = customers.last() + 1000
    GET("$host/orders/active") {
        queryParam("customerId", nonExistingCustomerId)
    } then {
        assertThat(code).isEqualTo(204)
    }
}

useCase("Test Get All Orders") {
    // Create a few orders first
    val customer1 = customers[2]
    val customer2 = customers[3]

    POST("$host/orders") {
        contentType("application/json")
        body("""
        {
            "customerId": $customer1,
            "cityId": ${cities.first()}
        }
        """.trimIndent())
    }

    POST("$host/orders") {
        contentType("application/json")
        body("""
        {
            "customerId": $customer2,
            "cityId": ${cities.last()}
        }
        """.trimIndent())
    }

    // Test getting all orders (no filter)
    GET("$host/orders") then {
        assertThat(code).isEqualTo(200)
        assertThat(jsonPath().readInt("$.content.length()")).isGreaterThanOrEqualTo(2)
    }

    // Test with filter by customer ID
    GET("$host/orders") {
        queryParam("customerId", customer1)
    } then {
        assertThat(code).isEqualTo(200)
        assertThat(jsonPath().readInt("$.content.length()")).isGreaterThanOrEqualTo(1)
        assertThat(jsonPath().readLong("$.content[0].customerId")).isEqualTo(customer1)
    }
}

useCase("Test Add Product to Order") {
    // Create an order first
    val orderId by POST("$host/orders") {
        contentType("application/json")
        body("""
        {
            "customerId": ${customers[4]},
            "cityId": ${cities[2]}
        }
        """.trimIndent())
    } then {
        jsonPath().readLong("$.id")
    }

    // Add product to order
    val orderLineId by POST("$host/orders/$orderId/lines") {
        contentType("application/json")
        body("""
        {
            "productId": ${products.first()},
            "amount": 2
        }
        """.trimIndent())
    } then {
        assertThat(code).isEqualTo(200)
        assertThat(jsonPath().readLong("$.productId")).isEqualTo(products.first())
        assertThat(jsonPath().readInt("$.amount")).isEqualTo(2)
        jsonPath().readLong("$.id")
    }

    // Verify product was added by checking active order
    GET("$host/orders/active") {
        queryParam("customerId", customers[4])
    } then {
        assertThat(code).isEqualTo(200)
        assertThat(jsonPath().readLong("$.id")).isEqualTo(orderId)
        assertThat(jsonPath().readInt("$.lines.length()")).isEqualTo(1)
        assertThat(jsonPath().readLong("$.lines[0].id")).isEqualTo(orderLineId)
    }
}

useCase("Test Change Product Amount") {
    // Create an order first
    val orderId by POST("$host/orders") {
        contentType("application/json")
        body("""
        {
            "customerId": ${customers[5]},
            "cityId": ${cities[2]}
        }
        """.trimIndent())
    } then {
        jsonPath().readLong("$.id")
    }

    // Add product to order
    val orderLineId by POST("$host/orders/$orderId/lines") {
        contentType("application/json")
        body("""
        {
            "productId": ${products[1]},
            "amount": 3
        }
        """.trimIndent())
    } then {
        jsonPath().readLong("$.id")
    }

    // Change product amount
    POST("$host/orders/$orderId/lines/$orderLineId") {
        contentType("application/json")
        body("""
        {
            "amount": 5
        }
        """.trimIndent())
    } then {
        assertThat(code).isEqualTo(200)
        assertThat(jsonPath().readInt("$.amount")).isEqualTo(5)
    }

    // Verify amount was changed
    GET("$host/orders/active") {
        queryParam("customerId", customers[5])
    } then {
        assertThat(code).isEqualTo(200)
        assertThat(jsonPath().readInt("$.lines[0].amount")).isEqualTo(5)
    }
}
