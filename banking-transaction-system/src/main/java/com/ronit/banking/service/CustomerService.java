package com.ronit.banking.service;

import com.ronit.banking.dto.CustomerRequestDTO;
import com.ronit.banking.dto.CustomerResponseDTO;
import com.ronit.banking.entity.Customer;
import com.ronit.banking.exception.CustomerNotFoundException;
import com.ronit.banking.repository.CustomerRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequest) {

        // DTO -> Entity
        Customer customer = new Customer();

        customer.setCustomerNumber(customerRequest.getCustomerNumber());
        customer.setFirstName(customerRequest.getFirstName());
        customer.setLastName(customerRequest.getLastName());
        customer.setEmail(customerRequest.getEmail());
        customer.setPhone(customerRequest.getPhone());

        // Save Entity into DB
        Customer savedCustomer = customerRepository.save(customer);

        // Entity -> Response DTO
        return mapToResponse(savedCustomer);
    }

    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with id: " + id));

        return mapToResponse(customer);
    }

    public CustomerResponseDTO updateCustomer(
            Long id,
            CustomerRequestDTO request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with id: " + id));

        customer.setCustomerNumber(request.getCustomerNumber());
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        customer.setUpdatedAt(LocalDateTime.now());

        Customer updatedCustomer = customerRepository.save(customer);

        return mapToResponse(updatedCustomer);
    }

    public void deleteCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with id: " + id));

        customerRepository.delete(customer);
    }

    private CustomerResponseDTO mapToResponse(Customer customer) {

        CustomerResponseDTO response = new CustomerResponseDTO();

        response.setId(customer.getId());
        response.setCustomerNumber(customer.getCustomerNumber());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setEmail(customer.getEmail());
        response.setPhone(customer.getPhone());
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedAt(customer.getUpdatedAt());

        return response;
    }
}