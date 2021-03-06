package com.crexos.model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.crexos.model.beans.Author;
import com.crexos.model.beans.Book;

/*
 *  L'impl�mentation concr�tre de Book pour la persistence en base de donn�e
 */
public class BookDAOImpl extends AbstractDAO implements BookDAO
{
	private final String COLUMN_ID = "id";
	private final String COLUMN_TITLE = "title";
	private final String COLUMN_AVAILABILITY = "availability";
	private final String COLUMN_PRICE = "price";
	private final String COLUMN_OVERVIEW = "overview";

	public BookDAOImpl(){}

	/*
	 * (non-Javadoc)
	 * @see com.crexos.model.dao.BookDAO#getById(int)
	 */
	@Override
	public Book getById(int id)
	{
		String query = "SELECT * FROM Book b JOIN Authors_Books ab ON ab.book_id = b.id WHERE id=?";

		Book book = new Book();
		PreparedStatement ps = null;
		ResultSet resultData = null;		
		try
		{
			ps = DAOFactory.getInstance().getPreparedStatement(query);
			ps.setInt(1, id);

			resultData = executeQuery(ps, "Impossible de r�cup�ter un livre par ID");

			while(resultData.next())
			{
				Author author = DAOFactory.getInstance().getAuthorDAO().getById(resultData.getInt("author_id"));

				if(resultData.isFirst())
				{
					book.setId(resultData.getInt(COLUMN_ID));
					book.setTitle(resultData.getString(COLUMN_TITLE));
					book.setAvailability(resultData.getBoolean(COLUMN_AVAILABILITY));
					book.setPrice(resultData.getFloat(COLUMN_PRICE));
					book.setOverview(resultData.getString(COLUMN_OVERVIEW));
				}

				book.addAuthor(author);
			}
		}
		catch (SQLException e)
		{
			System.err.println("Impossible de pr�parer la requ�te GetById livre");
			e.printStackTrace();
		}
		finally
		{			
			DAOFactory.getInstance().close(resultData, ps);
		}

		return book;
	}

	/*
	 * (non-Javadoc)
	 * @see com.crexos.model.dao.InterfaceDAO#exist(java.lang.Object)
	 */
	public int exist(Book book)
	{
		String query = "SELECT id FROM Book WHERE title = ?";

		PreparedStatement ps = null;
		ResultSet resultData = null;
		int bookID = 0;
		try
		{
			ps = DAOFactory.getInstance().getPreparedStatement(query);
			ps.setString(1, book.getTitle());

			resultData = executeQuery(ps, "Impossible de vrifier si un livre existe");

			if(resultData.next())
				bookID = resultData.getInt(1);
		}
		catch (SQLException e)
		{
			System.err.println("Impossible de pr�parer la requ�te Exist livre");
			e.printStackTrace();
		}
		finally
		{			
			DAOFactory.getInstance().close(resultData, ps);
		}

		return bookID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.crexos.model.dao.BookDAO#existJoin(int, int)
	 */
	public boolean existJoin(int bookId, int authorId)
	{
		String query = "SELECT * FROM authors_books WHERE author_id = ? AND book_id = ?";

		PreparedStatement ps = null;
		ResultSet resultData = null;
		boolean exist = false;
		try
		{
			ps = DAOFactory.getInstance().getPreparedStatement(query);
			ps.setInt(1, authorId);
			ps.setInt(2, bookId);

			resultData = executeQuery(ps, "Impossible de v�rifier si une jointure auteur livre existe");

			if(resultData.next())
				exist = true;
		}
		catch (SQLException e)
		{
			System.err.println("Impossible de pr�parer la requ�te Exist Jointure auteur livre");
			e.printStackTrace();
		}
		finally
		{			
			DAOFactory.getInstance().close(resultData, ps);
		}

		return exist;
	}

	/*
	 * (non-Javadoc)
	 * @see com.crexos.model.dao.BookDAO#getAll()
	 */
	@Override
	public List<Book> getAll()
	{
		String query = "SELECT * FROM Book b INNER JOIN Authors_books ab ON ab.book_id = b.id";

		PreparedStatement ps = null;
		ResultSet resultData = null;
		List<Book> books = new ArrayList<Book>();
		try
		{	
			ps = DAOFactory.getInstance().getPreparedStatement(query);

			resultData = executeQuery(ps, "Impossible de r�cup�ter liste de livre");
			Book book = null;
			while (resultData.next())
			{
				int idbook = resultData.getInt(COLUMN_ID);
				Author author = DAOFactory.getInstance().getAuthorDAO().getById(resultData.getInt("author_id"));

				if(!books.stream().anyMatch(b -> b.getId() == idbook))
				{
					book = new Book();
					book.setId(idbook);
					book.setTitle(resultData.getString(COLUMN_TITLE));
					book.setAvailability(resultData.getBoolean(COLUMN_AVAILABILITY));
					book.setOverview(resultData.getString(COLUMN_OVERVIEW));
					book.setPrice(resultData.getFloat(COLUMN_PRICE));

					book.addAuthor(author);
					books.add(book);
				}
				else
					books.stream().filter(b -> b.getId() == idbook).findFirst().get().addAuthor(author);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{			
			DAOFactory.getInstance().close(resultData, ps);
		}

		return books;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.crexos.model.dao.BookDAO#getSize()
	 */
	@Override
	public int getSize()
	{
		int size = 0;
		try
		{
			PreparedStatement ps = DAOFactory.getInstance().getPreparedStatement("SELECT COUNT(ID) FROM Book");
			ResultSet resultData = executeQuery(ps, "Impossible de r�cup�ter le nombre maximum de livre");
			if(resultData.next())
				size = resultData.getInt(1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return size;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.crexos.model.dao.BookDAO#getAll(int, int)
	 */
	@Override
	public List<Book> getAll(int offset, int noOfRecords)
	{
		String query = "SELECT * FROM Book b INNER JOIN Authors_books ab ON ab.book_id = b.id LIMIT ?, ?";
		
		PreparedStatement ps = null;
		ResultSet resultData = null;
		List<Book> books = new ArrayList<Book>();
		try
		{	
			ps = DAOFactory.getInstance().getPreparedStatement(query);
			ps.setInt(1, offset);
			ps.setInt(2, noOfRecords);
			
			resultData = executeQuery(ps, "Impossible de r�cup�ter liste de livre");
			Book book = null;
			while (resultData.next())
			{
				int idbook = resultData.getInt(COLUMN_ID);
				Author author = DAOFactory.getInstance().getAuthorDAO().getById(resultData.getInt("author_id"));

				if(!books.stream().anyMatch(b -> b.getId() == idbook))
				{
					book = new Book();
					book.setId(idbook);
					book.setTitle(resultData.getString(COLUMN_TITLE));
					book.setAvailability(resultData.getBoolean(COLUMN_AVAILABILITY));
					book.setOverview(resultData.getString(COLUMN_OVERVIEW));
					book.setPrice(resultData.getFloat(COLUMN_PRICE));

					book.addAuthor(author);
					books.add(book);
				}
				else
					books.stream().filter(b -> b.getId() == idbook).findFirst().get().addAuthor(author);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{			
			DAOFactory.getInstance().close(resultData, ps);
		}

		return books;
	}

	/*
	 * (non-Javadoc)
	 * @see com.crexos.model.dao.BookDAO#getAllSortedBy(java.lang.String, java.lang.String)
	 */
	@Override
	public List<Book> getAllSortedBy(String column, String mode)
	{
		String query = "SELECT * FROM Book b INNER JOIN Authors_books ab ON ab.book_id = b.id";

		if(mode.toUpperCase().equals("ASC") || mode.toUpperCase().equals("DESC"))
		{
			switch(column)
			{
			case COLUMN_TITLE:
				query += " ORDER BY " + column + " " + mode.toUpperCase();
				break;
			case COLUMN_PRICE:
				query += " ORDER BY " + column + " " + mode.toUpperCase();
				break;
			case COLUMN_AVAILABILITY:
				query += " ORDER BY " + column + " " + mode.toUpperCase();
				break;
			}
		}

		PreparedStatement ps = null;
		ResultSet resultData = null;
		List<Book> books = new ArrayList<Book>();
		try
		{	
			ps = DAOFactory.getInstance().getPreparedStatement(query);

			resultData = executeQuery(ps, "Impossible de r�cup�ter liste de livre");
			Book book = null;
			while (resultData.next())
			{
				int idbook = resultData.getInt(COLUMN_ID);
				Author author = DAOFactory.getInstance().getAuthorDAO().getById(resultData.getInt("author_id"));

				if(!books.stream().anyMatch(b -> b.getId() == idbook))
				{
					book = new Book();
					book.setId(idbook);
					book.setTitle(resultData.getString(COLUMN_TITLE));
					book.setAvailability(resultData.getBoolean(COLUMN_AVAILABILITY));
					book.setOverview(resultData.getString(COLUMN_OVERVIEW));
					book.setPrice(resultData.getFloat(COLUMN_PRICE));

					book.addAuthor(author);
					books.add(book);
				}
				else
					books.stream().filter(b -> b.getId() == idbook).findFirst().get().addAuthor(author);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{			
			DAOFactory.getInstance().close(resultData, ps);
		}

		return books;
	}

	/*
	 * (non-Javadoc)
	 * @see com.crexos.model.dao.BookDAO#getAllSortedBy(java.lang.String, java.lang.String, int, int)
	 */
	@Override
	public List<Book> getAllSortedBy(String column, String mode, int offset, int noOfRecords)
	{
		String query = "SELECT * FROM Book b INNER JOIN Authors_books ab ON ab.book_id = b.id";
		if(mode.toUpperCase().equals("ASC") || mode.toUpperCase().equals("DESC"))
		{
			switch(column)
			{
			case COLUMN_TITLE:
				query += " ORDER BY b." + COLUMN_TITLE + " " + mode.toUpperCase();
				query = query.replace("Book b", "(SELECT * FROM Book LIMIT ?,?) as b");
				break;
			case COLUMN_PRICE:
				query += " ORDER BY b." + COLUMN_TITLE + " " + mode.toUpperCase();
				query = query.replace("Book b", "(SELECT * FROM Book LIMIT ?,?) as b");
				break;
			case COLUMN_AVAILABILITY:
				query += " ORDER BY b." + COLUMN_TITLE + " " + mode.toUpperCase();
				query = query.replace("Book b", "(SELECT * FROM Book LIMIT ?,?) as b");
				break;
			}
		}

		PreparedStatement ps = null;
		ResultSet resultData = null;
		List<Book> books = new ArrayList<Book>();
		try
		{	
			ps = DAOFactory.getInstance().getPreparedStatement(query);

			ps.setInt(1, offset);
			ps.setInt(2, noOfRecords);
			
			resultData = executeQuery(ps, "Impossible de r�cup�ter liste de livre");
			Book book = null;
			
			while (resultData.next())
			{
				int idbook = resultData.getInt(COLUMN_ID);
				Author author = DAOFactory.getInstance().getAuthorDAO().getById(resultData.getInt("author_id"));

				if(!books.stream().anyMatch(b -> b.getId() == idbook))
				{
					book = new Book();
					book.setId(idbook);
					book.setTitle(resultData.getString(COLUMN_TITLE));
					book.setAvailability(resultData.getBoolean(COLUMN_AVAILABILITY));
					book.setOverview(resultData.getString(COLUMN_OVERVIEW));
					book.setPrice(resultData.getFloat(COLUMN_PRICE));

					book.addAuthor(author);
					books.add(book);
				}
				else
					books.stream().filter(b -> b.getId() == idbook).findFirst().get().addAuthor(author);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{			
			DAOFactory.getInstance().close(resultData, ps);
		}

		return books;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.crexos.model.dao.BookDAO#create(com.crexos.model.beans.Book)
	 */
	@Override
	public int create(Book book)
	{
		String query = "INSERT INTO Book (title, availability, price, overview) VALUES (?, ?, ?, ?)" ;

		PreparedStatement ps = null;
		ResultSet resultData = null;
		int bookID = exist(book);
		try
		{	
			ps = DAOFactory.getInstance().getPreparedStatement(query);
			ps.setString(1, book.getTitle());
			ps.setBoolean(2, book.getAvailability());
			ps.setFloat(3, book.getPrice());
			ps.setString(4, book.getOverview());

			if(bookID <= 0)
				bookID = executeUpdate(ps, "Aucune livre cr��");

			if(bookID != 0)
			{
				if(book.getAuthors().size() > 0)
				{
					for(Author author : book.getAuthors())
					{
						int authorID = DAOFactory.getInstance().getAuthorDAO().exist(author);
						//V�rifie si un auteur n'existe pas dans la BDD avant de le cr�er, si il existe on r�cup�re son ID pour la jointure avec le livre
						if(authorID == 0)
							authorID = DAOFactory.getInstance().getAuthorDAO().create(author);
							
						joinAuthor(bookID, authorID);
					}
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{			
			DAOFactory.getInstance().close(resultData, ps);
		}

		return bookID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.crexos.model.dao.BookDAO#update(com.crexos.model.beans.Book)
	 */
	@Override
	public void update(Book book)
	{
		String query = "UPDATE Book SET title = ?, availability = ?, price = ?, overview = ? WHERE id = ?";
		PreparedStatement ps = null;
		try
		{
			ps = DAOFactory.getInstance().getPreparedStatement(query);
			ps.setString(1, book.getTitle());
			ps.setBoolean(2, book.getAvailability());
			ps.setFloat(3, book.getPrice());
			ps.setString(4, book.getOverview());
			ps.setInt(5, book.getId());

			executeUpdate(ps, "Aucune MAJ livre effectu�e");
		}
		catch (SQLException e)
		{
			System.err.println("Impossible de pr�parer la requ�te MAJ livre");
			e.printStackTrace();
		}
		finally
		{			
			DAOFactory.getInstance().close(ps);
		}
	}

	@Override
	public void delete(int id)
	{
		String query = "DELETE FROM Book WHERE id=?";

		PreparedStatement ps = null;
		try
		{
			ps = DAOFactory.getInstance().getPreparedStatement(query);
			ps.setInt(1, id);

			if(deleteJoinAuthor(id))
				executeUpdate(ps, "Aucun livre a �t� supprim�");
		}
		catch (SQLException e)
		{
			System.err.println("Impossible de pr�parer la requ�te Supprimer livre");
			e.printStackTrace();
		}
		finally
		{			
			DAOFactory.getInstance().close(ps);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.crexos.model.dao.BookDAO#joinAuthor(int, int)
	 */
	public boolean joinAuthor(int book, int author)
	{
		String query = "INSERT INTO authors_books (author_id, book_id) VALUES (?, ?)";	

		if(existJoin(book, author))
			return true;
		
		PreparedStatement ps = null;
		boolean result = false;
		try
		{
			ps = DAOFactory.getInstance().getPreparedStatement(query);
			ps.setInt(1, author);
			ps.setInt(2, book);

			result = (executeUpdate(ps, "Aucune Jointure de livre-auteur cr��") == 0 ? false : true);
		}
		catch (SQLException e)
		{
			System.err.println("Impossible de pr�parer la requ�te Ajout jointure auteur_livre");
			e.printStackTrace();
		}
		finally
		{			
			DAOFactory.getInstance().close(ps);
		}

		return result;
	}

	/*
	 * 
	 */
	public boolean deleteJoinAuthor(int book)
	{
		String query = "DELETE FROM authors_books WHERE book_id =?" ;

		PreparedStatement ps = null;
		boolean result = false;

		try
		{
			ps = DAOFactory.getInstance().getPreparedStatement(query);
			ps.setInt(1, book);

			result = (executeUpdate(ps, "Aucune Jointure de livre-auteur supprim�") == 0 ? false : true);
		}
		catch (SQLException e)
		{
			System.err.println("Impossible de pr�parer la requ�te Suppression jointure auteur_livre");
			e.printStackTrace();
		}
		finally
		{			
			DAOFactory.getInstance().close(ps);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.crexos.model.dao.BookDAO#deleteJoinAuthorBook(int, int)
	 */
	public boolean deleteJoinAuthorBook(int authorId, int bookId)
	{
		String query = "DELETE FROM authors_books WHERE author_id =? AND book_id =?" ;

		PreparedStatement ps = null;
		boolean result = false;

		try
		{
			ps = DAOFactory.getInstance().getPreparedStatement(query);
			ps.setInt(1, authorId);
			ps.setInt(2, bookId);

			result = (executeUpdate(ps, "Aucune Jointure de livre-auteur supprim�") == 0 ? false : true);
		}
		catch (SQLException e)
		{
			System.err.println("Impossible de pr�parer la requ�te Suppression jointure auteur_livre");
			e.printStackTrace();
		}
		finally
		{			
			DAOFactory.getInstance().close(ps);
		}

		return result;
	}
}
