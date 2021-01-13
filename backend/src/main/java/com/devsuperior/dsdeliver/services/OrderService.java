package com.devsuperior.dsdeliver.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsdeliver.dto.OrderDTO;
import com.devsuperior.dsdeliver.dto.ProductDTO;
import com.devsuperior.dsdeliver.entities.Order;
import com.devsuperior.dsdeliver.entities.OrderStatus;
import com.devsuperior.dsdeliver.entities.Product;
import com.devsuperior.dsdeliver.repositories.OrderRepository;
import com.devsuperior.dsdeliver.repositories.ProductRepository;

//para registrar o componente infetavel, coloca a anotiation @Component, mas é meçllçhor @SErvice
@Service
public class OrderService {
	@Autowired // injeção de dependências
	private OrderRepository repository ;
	
	@Autowired
	private ProductRepository productRepository;
	
	
	//pra nã odar lock no banco
	@Transactional(readOnly = true)
	public List<OrderDTO> findAll(){
		List<Order> list = repository.findOdersWithProducts();
		
		return list.stream().map(x -> new OrderDTO(x)).collect(Collectors.toList());
		
	}

	@Transactional()
	public OrderDTO insert(OrderDTO dto){
		
		Order order = new Order(null, dto.getAddress(), dto.getLatitude(),dto.getLongitude(),
				Instant.now(), OrderStatus.PENDING);
		
		for (ProductDTO p : dto.getProducts()) {
			Product product = productRepository.getOne(p.getId());
			order.getProduts().add(product);			
		}
		
		order = repository.save(order);
		return new OrderDTO(order);
		
	}
	@Transactional()
	public OrderDTO setDelivered(Long id){
		Order order = repository.getOne(id);
		order.setStatus(OrderStatus.DELIVERED);
		order = repository.save(order);
		return new OrderDTO(order);
		
		
	}
}
