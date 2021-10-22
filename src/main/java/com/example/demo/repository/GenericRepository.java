package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import com.example.demo.entity.AbstractEntity;

/**
 * 
 * @author Herick Melo
 *
 */
@NoRepositoryBean
public interface GenericRepository<T extends AbstractEntity> extends JpaRepository<T, Long> {

	@Override
	@Query(value = "select e from #{#entityName} e where e.active = true")
	List<T> findAll();

	@Override
	@Query(value = "select e from #{#entityName} e where e.id = ?1 and e.active = true")
	Optional<T> findById(Long id);

/*
	@Override
	@Transactional
	@Modifying
	@Query(value = "UPDATE #{#entityName} SET active=false where id = ?1")
	void deleteById(Integer id);
*/
	
	@Override
	@Transactional
	default void deleteById(Long long1) {
		Optional<T> entity = findById(long1);
		entity.get().setActive(false);
		save(entity.get());
	}	

	@Override
	@Transactional
	default void delete(T obj) {
		obj.setActive(false);
		save(obj);
	}
	
/*
	@Override
	@Transactional
	@Modifying
	@Query(value = "UPDATE #{#entityName} e SET e.active=false where e = ?1")
	void delete(T entity);
*/
	
	@Override
	default void deleteAll(Iterable<? extends T> arg0) {
		arg0.forEach(entity -> {
			deleteById(entity.getId());
		});
	}
	
	@Query(value="select count(e) from #{#entityName} e where e.active = true")
	long countActive();

}

