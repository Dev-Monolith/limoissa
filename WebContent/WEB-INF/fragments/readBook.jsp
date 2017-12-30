<p class="label">Titre : <c:out value="${book.title}"/></p>
<p class="label">Prix : <c:out value="${book.price}"/></p>
<p class="label">R�sum� : <textarea cols="40" rows="6" disabled><c:out value="${book.overview}"/></textarea></p>
<p class="label">Disponibilit� : <c:if test="${book.availability == true}">En stock</c:if></p>
<p class="label">Auteur(s) : 
	<c:forEach items="${book.authors}" var="author" varStatus="loop">
		${author.firstName} ${author.lastName}<c:if test="${!loop.last}">,</c:if>
	</c:forEach>
</p>
<br />
<a href="<c:url value='/books/edit?book-id=${book.id}'/>">Editer</a>
<a href="<c:url value='/books'/>">Retour Catalogue livre</a>