package org.example.repository;

import org.springframework.stereotype.Repository;
import org.example.exception.NotFoundException;
import org.example.model.Post;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class PostRepository implements IPostRepository {
    private AtomicInteger postId = new AtomicInteger(1);
    private List<Post> posts = new CopyOnWriteArrayList();

    public List<Post> all() {
        return posts.stream()
                .filter(post -> !post.deleted())
                .collect(Collectors.toList());
    }

    public Optional<Post> getById(long id) {
        return posts.stream()
                .filter(post -> post.getId() == id)
                .filter(post -> !post.deleted())
                .findFirst();
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            post.setId(postId.longValue());
            postId.addAndGet(1);
        } else {
            for (Post searchPost: this.posts) {
                if (post.getId() == searchPost.getId() && !searchPost.deleted()){
                    searchPost.setContent(post.getContent());
                    return searchPost;
                }
            }
            throw new NotFoundException("Post not found");
        }
        this.posts.add(post);
        return post;
    }

    public void removeById(long id) {
        for (int i = 0; i < this.posts.size(); i++) {
            Post searchPost = this.posts.get(i);
            if (searchPost.getId() == id) {
                searchPost.delete();
            }
        }
    }
}