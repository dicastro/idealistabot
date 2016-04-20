package es.qopuir.idealistabot.repo;

import org.springframework.data.repository.CrudRepository;

import es.qopuir.idealistabot.model.Chat;

public interface ChatRepository extends CrudRepository<Chat, Integer> {
}