package com.poly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.entity.Loai;

import java.util.List;
import java.util.Optional;


public interface LoaiRepository extends JpaRepository<Loai, Integer>{
	Optional<Loai> findByTenLoai(String tenLoai);
}
