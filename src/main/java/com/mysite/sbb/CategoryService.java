package com.mysite.sbb;

import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return this.categoryRepository.findAll();
    }

    public void createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        this.categoryRepository.save(category);
    }

    public Optional<Category> getCategoryById(Integer id) {
        return this.categoryRepository.findById(id);
    }
}
