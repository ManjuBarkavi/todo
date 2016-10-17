package com.todo.jdo;

import java.io.Serializable;


import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


@PersistenceCapable
public class TodoListJDO implements Serializable {
	
	private static final long serialVersionUID = 5111993021645L;
	
	
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private String contactKey;
	
	@Persistent
	private String title;
	
	@Persistent
	private Long dateAdded;
	
	@Persistent 
	private Long dateCompleted;
	
	@Persistent
	private Boolean isDone;
	
	@Persistent
	private String status;
	
	@Persistent
	private Integer order;
	
	@Persistent
	private Integer type;
	
	@Persistent
	private Integer score;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContactKey() {
		return contactKey;
	}

	public void setContactKey(String contactKey) {
		this.contactKey = contactKey;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Long dateAdded) {
		this.dateAdded = dateAdded;
	}

	public Long getDateCompleted() {
		return dateCompleted;
	}

	public void setDateCompleted(Long dateCompleted) {
		this.dateCompleted = dateCompleted;
	}

	public Boolean getIsDone() {
		return isDone;
	}

	public void setIsDone(Boolean isDone) {
		this.isDone = isDone;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
	
	
}	
