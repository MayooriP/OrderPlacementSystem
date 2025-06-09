package com.restaurant.ordersystem.controller;

import com.restaurant.ordersystem.model.Customer;
import com.restaurant.ordersystem.repository.CustomerRepository;
import com.restaurant.ordersystem.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // GET all customers
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        logger.info("Retrieving all customers");
        List<Customer> customers = customerRepository.findAll();
        logger.info("Retrieved {} customers", customers.size());
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    // GET customer by ID
    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer customerId) {
        logger.info("Retrieving customer with ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        logger.info("Retrieved customer with ID: {}", customerId);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    // POST - Create new customer
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        logger.info("Creating new customer: {}", customer.getFullName());
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer created with ID: {}", savedCustomer.getCustomerId());
        return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
    }

    // PUT - Update customer
    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Integer customerId,
                                                   @RequestBody Customer updatedCustomerData) {
        logger.info("Updating customer with ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        customer.setFullName(updatedCustomerData.getFullName());
        customer.setFirstName(updatedCustomerData.getFirstName());
        customer.setEmail(updatedCustomerData.getEmail());
        customer.setDob(updatedCustomerData.getDob());
        customer.setGender(updatedCustomerData.getGender());
        customer.setEncryptedPhoneNumber(updatedCustomerData.getEncryptedPhoneNumber());
        customer.setEncryptedEmail(updatedCustomerData.getEncryptedEmail());
        customer.setStatus(updatedCustomerData.getStatus());
        customer.setLastModifiedBy(updatedCustomerData.getLastModifiedBy());
        customer.setLastModifiedDateTime(updatedCustomerData.getLastModifiedDateTime());

        Customer updatedCustomer = customerRepository.save(customer);
        logger.info("Customer updated with ID: {}", customerId);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    // DELETE - Delete customer
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer customerId) {
        logger.info("Deleting customer with ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        customerRepository.delete(customer);
        logger.info("Customer deleted with ID: {}", customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
