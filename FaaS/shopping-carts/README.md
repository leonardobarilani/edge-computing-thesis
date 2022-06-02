# Shopping Carts Use Case

## Topology

1. Cloud
2. Country
3. City

## Functions

| Name                  | Type              | Where     | Callers                                                   |
|-----------------------|-------------------|-----------|-----------------------------------------------------------|
| `shopping-cart`       | Offloadable       | City      | Called by users that want to add something to their cart  |
| `products-counter`    | ReceivePropagate  | Country   | Called by `shopping-cart` through `propagate()`           |

* `shopping-cart` stores the products picked by the user and propagates each ned add to the `products-counter`
* `products-counter` just receives each added product and increase the counter of that product

## Database

* `shopping-cart` uses sessions to represent each users' shopping carts. Sessions expire after 24 hours
* `products-counter` uses the local database to store the products count. Doesn't expire
