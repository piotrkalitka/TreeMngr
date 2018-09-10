package pl.piotrkalitka.TreeMngr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.piotrkalitka.TreeMngr.model.Item;

@Repository
public interface ItemsRepository extends JpaRepository<Item, Long> {

    Item findByParentId(Long parentId);

    boolean existsByParentId(Long parentId);

}
