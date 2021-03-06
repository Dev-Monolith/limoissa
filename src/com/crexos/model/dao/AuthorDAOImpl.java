package com.crexos.model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.crexos.model.beans.Author;
import com.crexos.model.utils.Country;

/*
 *  L'impl�mentation concr�tre de Author pour la persistence en base de donn�e
 */
public class AuthorDAOImpl extends AbstractDAO implements AuthorDAO
{
	private final String COLUMN_ID = "id";
	private final String COLUMN_FIRSTNAME = "firstname";
	private final String COLUMN_LASTNAME = "lastname";
	private final String COLUMN_NATIVE_COUNTRY = "native_country";

	public AuthorDAOImpl(){}

	/*
	 * @see com.crexos.model.dao.AuthorDAO#getById(int)
	 */
	@Override
	public Author getById(int id)
	{
		Author author = new Author();
		String query = "SELECT * FROM Author WHERE id=?";

		PreparedStatement ps = null;
		ResultSet resultData = null;
		try
		{
			ps = DAOFactory.getInstance().getPreparedStatement(query);
			ps.setInt(1, id);

			resultData = executeQuery(ps, "Impossible de r�cup�ter un auteur par ID");

			if(resultData != null && resultData.next())
			{
				author.setId(resultData.getInt(COLUMN_ID));
				author.setFirstname(resultData.getString(COLUMN_FIRSTNAME));
				author.setLastName(resultData.getString(COLUMN_LASTNAME));
				author.setNativeCountry(Country.valueOf(resultData.getString(COLUMN_NATIVE_COUNTRY)));
			}
		}
		catch(SQLException e)
		{
			System.err.println("Impossible de pr�parer la requ�te getById auteur");
			e.printStackTrace();
		}
		finally
		{
			DAOFactory.getInstance().close(resultData, ps);
		}

		return author;
	}
	
	/*
	 * @see com.crexos.model.dao.InterfaceDAO#exist(java.lang.Object)
	 */
	@Override
	public int exist(Author author)
	{
		String query = "SELECT id FROM Author WHERE firstname = ? AND lastname = ?";

		PreparedStatement ps = null;
		ResultSet resultData = null;
		int authorID = 0;
		try
		{
			ps = DAOFactory.getInstance().getPreparedStatement(query);
			ps.setString(1, author.getFirstName());
			ps.setString(2, author.getLastName());

			resultData = executeQuery(ps, "Impossible de v�rifier si un auteur existe");

			if(resultData.next())
				authorID = resultData.getInt(1);
		}
		catch (SQLException e)
		{
			System.err.println("Impossible de pr�parer la requ�te Exist auteur");
			e.printStackTrace();
		}
		finally
		{			
			DAOFactory.getInstance().close(resultData, ps);
		}

		return authorID;
	}

	/*
	 * @see com.crexos.model.dao.AuthorDAO#getAll()
	 */
	@Override
	public List<Author> getAll()
	{
		List<Author> authors = new ArrayList<Author>();

		String query = "SELECT * FROM Author";

		PreparedStatement ps = null;
		ResultSet resultData = null;
		try
		{
			ps = DAOFactory.getInstance().getPreparedStatement(query);

			resultData = executeQuery(ps, "Impossible de r�cup�ter liste des auteurs");

			if(resultData != null)
				while (resultData.next())
				{

					Author author = new Author();
					author.setId(resultData.getInt(COLUMN_ID));
					author.setFirstname(resultData.getString(COLUMN_FIRSTNAME));
					author.setLastName(resultData.getString(COLUMN_LASTNAME));
					author.setNativeCountry(Country.valueOf(resultData.getString(COLUMN_NATIVE_COUNTRY)));

					authors.add(author);
				}
		}
		catch(SQLException e)
		{
			System.err.println("Impossible de pr�parer la requ�te getAll auteur");
			e.printStackTrace();
		}
		finally
		{
			DAOFactory.getInstance().close(resultData, ps);
		}

		return authors;
	}

	/*
	 * @see com.crexos.model.dao.AuthorDAO#create(com.crexos.model.beans.Author)
	 */
	@Override
	public int create(Author author)
	{
		String query = "INSERT INTO Author (firstname, lastname, native_country) VALUES (?, ?, ?)";
		
		PreparedStatement ps = null;
		int authorID = exist(author);
		
		if(authorID > 0)
			return authorID;
		
		try
		{
			ps = DAOFactory.getInstance().getPreparedStatement(query);
			ps.setString(1, author.getFirstName());
			ps.setString(2, author.getLastName());
			ps.setString(3, "" + author.getNativeCountry());//Astuce poru convertir ENUM en string
			
			authorID = executeUpdate(ps, "Aucun auteur ajout�");
		}
		catch(SQLException e)
		{
			System.err.println("Impossible de pr�parer la requ�te create auteur");
			e.printStackTrace();
		}
		finally
		{
			DAOFactory.getInstance().close(ps);
		}

		return authorID;
	}

	/*
	 * @see com.crexos.model.dao.AuthorDAO#update(com.crexos.model.beans.Author)
	 */
	@Override
	public void update(Author author)
	{
		String query = "UPDATE Author SET firstname = ?, lastname = ?, native_country = ? WHERE id = ?";
		
		PreparedStatement ps = null;
		try
		{
			ps = DAOFactory.getInstance().getPreparedStatement(query);
			ps.setString(1, author.getFirstName());
			ps.setString(2, author.getLastName());
			ps.setString(3, "" + author.getNativeCountry());//Astuce poru convertir ENUM en string
			ps.setInt(4, author.getId());
			
			executeUpdate(ps, "Aucune MAJ auteur effectu�e");
		}
		catch(SQLException e)
		{
			System.err.println("Impossible de pr�parer la requ�te update auteur");
			e.printStackTrace();
		}
		finally
		{
			DAOFactory.getInstance().close(ps);
		}
	}

	/*
	 * @see com.crexos.model.dao.AuthorDAO#delete(int)
	 */
	@Override
	public void delete(int id)
	{
		String query = "DELETE FROM Author WHERE id=?";

		PreparedStatement ps = null;
		try
		{
			ps = DAOFactory.getInstance().getPreparedStatement(query);
			ps.setInt(1, id);
			
			executeUpdate(ps, "Aucun auteur supprim�");
		}
		catch(SQLException e)
		{
			System.err.println("Impossible de pr�parer la requ�te delete auteur");
			e.printStackTrace();
		}
		finally
		{
			DAOFactory.getInstance().close(ps);
		}
	}
}
