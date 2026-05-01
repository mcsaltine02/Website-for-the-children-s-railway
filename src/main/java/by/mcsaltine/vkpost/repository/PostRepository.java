package by.mcsaltine.vkpost.repository;

import by.mcsaltine.vkpost.model.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Long> {
}
