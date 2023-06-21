package org.example.repository;

import org.springframework.stereotype.Repository;
import org.example.exception.NotFoundException;
import org.example.model.Post;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class PostRepository implements IPostRepository {
    private AtomicInteger postId = new AtomicInteger(1);
    private List<Post> posts = new CopyOnWriteArrayList();

    public List<Post> all() {
        return this.posts;
    }

    public Optional<Post> getById(long id) {
        return posts.stream().filter(post -> post.getId() == id).findFirst();
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            post.setId(postId.longValue());
            postId.addAndGet(1);
        } else {
            for (Post searchPost: this.posts) {
                if (post.getId() == searchPost.getId()){
                    searchPost.setContent(post.getContent());
                    return searchPost;
                }
            }
            // если не нашли - кидаем исключение
            throw new NotFoundException("Post not found");
        }
        this.posts.add(post);
        return post;
    }

    public void removeById(long id) {
        for (int i = 0; i < this.posts.size(); i++) {
            Post searchPost = this.posts.get(i);
            if (searchPost.getId() == id) {
                this.posts.remove(i);
            }
        }
    }
}