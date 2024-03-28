package com.turkcell.rentACar1.business.concretes;

import com.turkcell.rentACar1.business.abstracts.BrandService;
import com.turkcell.rentACar1.business.dtos.requests.brands.CreateBrandRequest;
import com.turkcell.rentACar1.business.dtos.requests.brands.UpdateBrandRequest;
import com.turkcell.rentACar1.business.dtos.responses.brands.CreatedBrandResponse;
import com.turkcell.rentACar1.business.dtos.responses.brands.GetAllBrandsListItemDto;
import com.turkcell.rentACar1.business.dtos.responses.brands.GetBrandResponse;
import com.turkcell.rentACar1.business.dtos.responses.brands.UpdatedBrandResponse;
import com.turkcell.rentACar1.business.rules.BrandBusinessRules;
import com.turkcell.rentACar1.core.utilities.mapping.ModelMapperService;
import com.turkcell.rentACar1.dataAccess.abstracts.BrandRepository;
import com.turkcell.rentACar1.entities.concretes.Brand;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service

public class BrandManager implements BrandService {
    private BrandRepository brandRepository;
    private ModelMapperService modelMapperService;
    private BrandBusinessRules brandBusinessRules;

    @Override
    public CreatedBrandResponse add(CreateBrandRequest createBrandRequest) {
        brandBusinessRules.brandNameCanNotBeDuplicatedWhenInserted(createBrandRequest.getName());

        Brand brand = this.modelMapperService.forRequest().map(createBrandRequest, Brand.class);
        brand.setCreatedDate(LocalDateTime.now());

        Brand createdBrand = brandRepository.save(brand);
        return modelMapperService.forResponse().map(createdBrand, CreatedBrandResponse.class);

    }

    @Override
    public UpdatedBrandResponse update(int id, UpdateBrandRequest updateBrandRequest) {
        brandBusinessRules.brandIdShouldBeExist(id);
        brandBusinessRules.brandNameCanNotBeDuplicatedWhenUpdated(id, updateBrandRequest.getName());
        Brand brandToUpdate = modelMapperService.forRequest().map(updateBrandRequest, Brand.class);
        brandToUpdate.setId(id);
        Brand updatedBrand = brandRepository.save(brandToUpdate);
        return modelMapperService.forResponse().map(updatedBrand, UpdatedBrandResponse.class);
    }

    @Override
    public void delete(int id) {
        Optional<Brand> foundOptionalBrand = brandRepository.findById(id);
        brandBusinessRules.brandShouldBeExist(foundOptionalBrand);
        brandRepository.delete(foundOptionalBrand.get());
    }

    @Override
    public List<GetAllBrandsListItemDto> getAll() {
        List<Brand> brands = brandRepository.findAll();
        // example of using stream api
        return brands.stream()
                .map(brand -> this.modelMapperService.forResponse()
                        .map(brand, GetAllBrandsListItemDto.class))
                .toList();
    }

    @Override
    public GetBrandResponse get(int id) {
        Optional<Brand> foundOptionalBrand = brandRepository.findById(id);
        brandBusinessRules.brandShouldBeExist(foundOptionalBrand);
        return modelMapperService.forResponse().map(foundOptionalBrand.get(), GetBrandResponse.class);
    }
}