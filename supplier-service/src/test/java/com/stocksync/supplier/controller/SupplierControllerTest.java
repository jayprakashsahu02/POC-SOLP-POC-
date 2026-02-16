package com.stocksync.supplier.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stocksync.supplier.dto.Response;
import com.stocksync.supplier.dto.SupplierDTO;
import com.stocksync.supplier.service.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupplierController.class)
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupplierService supplierService;

    @Autowired
    private ObjectMapper objectMapper;

    private SupplierDTO supplierDTO;

    @BeforeEach
    void setUp() {
        supplierDTO = new SupplierDTO();
        supplierDTO.setId(1L);
        supplierDTO.setName("Tata Pvt Ltd");
        supplierDTO.setContactInfo("9931116045");
        supplierDTO.setAddress("Jamshedpur");
    }

    // POST /add 
    @Test
    void testAddSupplierSuccess() throws Exception {
        Response response = Response.builder()
                .status(200)
                .message("Supplier Saved Successfully")
                .build();

        when(supplierService.addSupplier(any(SupplierDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/suppliers/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(supplierDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Supplier Saved Successfully"));
    }

    // GET /all
    @Test
    void testGetAllSuppliersSuccess() throws Exception {
        Response response = Response.builder()
                .status(200)
                .message("All Suppliers Retrieved")
                .data(java.util.List.of(supplierDTO))
                .build();

        when(supplierService.getAllSupplier()).thenReturn(response);

        mockMvc.perform(get("/api/v1/suppliers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("All Suppliers Retrieved"))
                .andExpect(jsonPath("$.data[0].name").value("Tata Pvt Ltd"));
    }

    // GET /{id}
    @Test
    void testGetSupplierByIdSuccess() throws Exception {
        Response response = Response.builder()
                .status(200)
                .message("Supplier Found")
                .data(supplierDTO)
                .build();

        when(supplierService.getSupplierById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/suppliers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Supplier Found"))
                .andExpect(jsonPath("$.data.name").value("Tata Pvt Ltd"));
    }

    @Test
    void testGetSupplierByIdNotFound() throws Exception {
        Response response = Response.builder()
                .status(404)
                .message("Supplier Not Found")
                .build();

        when(supplierService.getSupplierById(99L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/suppliers/99"))
                .andExpect(status().isOk()) // Controller always returns 200 with Response body
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Supplier Not Found"));
    }

    // PUT /update/{id} 
    @Test
    void testUpdateSupplierSuccess() throws Exception {
        Response response = Response.builder()
                .status(200)
                .message("Supplier Updated Successfully")
                .build();

        when(supplierService.updateSupplier(eq(1L), any(SupplierDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/suppliers/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(supplierDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Supplier Updated Successfully"));
    }

    // DELETE /delete/{id}
    @Test
    void testDeleteSupplierSuccess() throws Exception {
        Response response = Response.builder()
                .status(200)
                .message("Supplier Deleted Successfully")
                .build();

        when(supplierService.deleteSupplier(1L)).thenReturn(response);

        mockMvc.perform(delete("/api/v1/suppliers/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Supplier Deleted Successfully"));
    }

    @Test
    void testDeleteSupplierNotFound() throws Exception {
        Response response = Response.builder()
                .status(404)
                .message("Supplier Not Found")
                .build();

        when(supplierService.deleteSupplier(99L)).thenReturn(response);

        mockMvc.perform(delete("/api/v1/suppliers/delete/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Supplier Not Found"));
    }
}
