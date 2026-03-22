package in.manikanta.moneymanager.controller;

import in.manikanta.moneymanager.dto.CategoryDTO;
import in.manikanta.moneymanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping({ "/categories"})
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategory = categoryService.saveCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategories(){
        List<CategoryDTO> categories =categoryService.getCategoriesForCurrentUser();
        return ResponseEntity.ok(categories);
    }

//    public List<CategoryDTO> getCategoriesByType
}
