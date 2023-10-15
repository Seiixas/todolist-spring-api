package br.com.mateus.todolist.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IUserRepository extends JpaRepository<UserModel, UUID> {
  public Optional<UserModel> findByUsername(String username);
}
