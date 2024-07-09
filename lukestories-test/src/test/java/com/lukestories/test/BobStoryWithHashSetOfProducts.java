package com.lukestories.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class BobStoryWithHashSetOfProducts {

    @Test
    void createAndCheckProducts() {
        // Bob's HashSet to store e-commerce data (products)
        Set<Product> products = new HashSet<>(10, 0.5f);

        // Adding products to the HashSet
        products.add(new Product("Laptop", 1200.00));
        products.add(new Product("Smartphone", 800.00));
        products.add(new Product("Tablet", 500.00));
        products.add(new Product("Smartwatch", 300.00));
        products.add(new Product("Headphones", 150.00));
        products.add(new Product("Camera", 700.00)); // Collision with Tablet, same bucket index
        products.add(new Product("Keyboard", 80.00)); // Collision with Headphones, same bucket index

        Product test4Product = new Product("Smartwatch", 300.00);
        Product test4ProductInCollision = new Product("Camera", 700.00);
        Product test4ProductInCollision2 = new Product("Smartphone", 800.00);
        short initialCapacity = 16-1;
        int test4ProductInCollisionBucketIndex = test4ProductInCollision.hashCode() & initialCapacity;
        int test4ProductInCollisionBucketIndex2 = test4ProductInCollision2.hashCode() & initialCapacity;

        assertThat(test4ProductInCollision.hashCode() & initialCapacity).isEqualTo(5);
        assertThat(test4ProductInCollisionBucketIndex).isEqualTo(test4ProductInCollisionBucketIndex2);
        assertThat(products.contains(test4Product)).isTrue();

        // Visualizing the hash table with buckets
        visualizeHashTable(products);
    }

    // Product class representing e-commerce products
    static class Product {
        private String name;
        private double price;

        public Product(String name, double price) {
            this.name = name;
            this.price = price;
        }

        @Override
        public int hashCode() {
            return name.hashCode(); // Simplified hashCode for demonstration
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Product product = (Product) obj;
            return name.equals(product.name);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // Visualizing the hash table with buckets
    static void visualizeHashTable(Set<Product> products) {
        // Initialize an array to represent buckets
        Object[] buckets = new Object[16]; // Initial capacity of 16 for demonstration

        // Iterate over the products and place them in buckets based on their hash code
        for (Product product : products) {
            int hashCode = product.hashCode();
            int bucketIndex = hashCode & (buckets.length - 1); // Calculate bucket index with bitwise operation AND 

            if (buckets[bucketIndex] == null) {
                buckets[bucketIndex] = product;
            } else {
                // Handle collision using linked list
                if (buckets[bucketIndex] instanceof LinkedList) {
                    ((LinkedList<Product>) buckets[bucketIndex]).add(product);
                } else {
                    LinkedList<Product> collisionList = new LinkedList<>();
                    collisionList.add((Product) buckets[bucketIndex]);
                    collisionList.add(product);
                    buckets[bucketIndex] = collisionList;
                }
            }
        }

        // Print out the contents of each bucket
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i] == null) {
                System.out.println("Bucket " + i + ": [empty]");
            } else if (buckets[i] instanceof LinkedList) {
                System.out.println("Bucket " + i + ": " + buckets[i]);
            } else {
                System.out.println("Bucket " + i + ": " + buckets[i]);
            }
        }

        // Comment to clarify that HashSet contains HashMap with default value
        System.out.println("\nNote: HashSet internally contains a HashMap with default value.");
    }
}
