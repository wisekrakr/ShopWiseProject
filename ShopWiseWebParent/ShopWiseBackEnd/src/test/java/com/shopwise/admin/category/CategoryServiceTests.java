package com.shopwise.admin.category;

import com.shopwise.admin.entity.repositories.CategoryRepository;
import com.shopwise.admin.entity.services.CategoryService;
import com.shopwise.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    @MockBean
    private CategoryRepository repository; // create a fake repository

    @InjectMocks
    private CategoryService service; // inject the repository into the service

    @Test
    public void testCheckUniquenessReturnDuplicateName(){
        Integer id = null;
        String name = "Video Games";
        String alias = "bla";

        Category category = new Category(id, name, alias);

        Mockito.when(repository.findByName(name)).thenReturn(category);
        Mockito.when(repository.findByAlias(alias)).thenReturn(null);

        String result = service.checkUniqueness(id, name, alias);

        assertThat(result).isEqualTo("Duplicate");
    }

    @Test
    public void testCheckUniquenessReturnDuplicateAlias(){
        Integer id = null;
        String name = "Bla";
        String alias = "video_games";

        Category category = new Category(id, name, alias);

        Mockito.when(repository.findByName(name)).thenReturn(null);
        Mockito.when(repository.findByAlias(alias)).thenReturn(category);

        String result = service.checkUniqueness(id, name, alias);

        assertThat(result).isEqualTo("Duplicate");
    }
}
