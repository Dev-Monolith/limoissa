package com.crexos.model.dao;

import java.util.List;

public interface InterfaceDAO<T>
{
	public T getById(int id);
	public void delete(int id);
	public void update(T obj);
	public void create(T obj);
	public List<T> getAll();
}