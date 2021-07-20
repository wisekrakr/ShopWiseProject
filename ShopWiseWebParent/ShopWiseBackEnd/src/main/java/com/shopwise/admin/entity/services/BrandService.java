package com.shopwise.admin.entity.services;

import com.shopwise.admin.entity.repositories.BrandRepository;
import com.shopwise.admin.entity.services.impl.IBrandService;
import com.shopwise.admin.utils.BrandNotFoundException;
import com.shopwise.common.entity.Brand;
import com.shopwise.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * BrandService holds all the "Actions" for brands
 */

@Service
@Transactional
public class BrandService implements IBrandService {

    public static final int BRANDS_PER_PAGE = 6;

    @Autowired
    private BrandRepository repository;

    @Override
    public List<Brand> getAll(){
        return (List<Brand>) repository.findAll();
    }

    @Override
    public Page<Brand> getByPage(int pageNumber, String sortField, String sortDirection, String keyword){
        Sort sort = Sort.by(sortField);
        sort = sortDirection.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber -1, BRANDS_PER_PAGE, sort);

        if(keyword != null){
            return repository.findAllWithKeyword(keyword, pageable);
        }

        return repository.findAll(pageable);
    }

    @Override
    public Brand save(Brand brand) {
        return repository.save(brand);
    }

    @Override
    public Brand update(Brand brand){
        Brand brandInDb = repository.findById(brand.getId()).get();

        if(brand.getLogo() != null){
            brandInDb.setLogo(brand.getLogo());
        }

        brandInDb.setName(brand.getName());

        return repository.save(brandInDb);
    }

    @Override
    public Brand getById(Integer id) throws BrandNotFoundException {
        try {
            return repository.findById(id).get();

        }catch (NoSuchElementException e){
            throw new BrandNotFoundException("Could not find any brand with ID: " + id);
        }
    }

    @Override
    public Brand getByName(String name) {
            return repository.findByName(name);
    }

    @Override
    public void delete(Integer id) throws BrandNotFoundException {

        Long countById = repository.countById(id);

        if(countById == null || countById == 0){
            throw new BrandNotFoundException("Could not find any brand with ID: " + id);
        }

        repository.deleteById(id);
    }

    @Override
    public String checkUniqueness(Integer id, String name) {
        boolean isNew = (id == null || id == 0);

        Brand byName = repository.findByName(name);

        if (isNew) {
            if (byName != null) return "Duplicate";
        } else {
            if (byName != null && !byName.getId().equals(id)) return "Duplicate";
        }

        return "OK";
    }

}
