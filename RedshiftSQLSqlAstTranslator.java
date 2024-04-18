package com.workflow.callback.config;

import org.hibernate.dialect.PostgreSQLSqlAstTranslator;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.sql.ast.tree.Statement;
import org.hibernate.sql.ast.tree.select.QueryPart;
import org.hibernate.sql.exec.spi.JdbcOperation;

public class RedshiftSQLSqlAstTranslator<T extends JdbcOperation> extends PostgreSQLSqlAstTranslator<T> {

	public RedshiftSQLSqlAstTranslator(SessionFactoryImplementor sessionFactory, Statement statement) {
		super(sessionFactory, statement);
	}

	@Override
	public void visitOffsetFetchClause(QueryPart queryPart) {
		renderLimitOffsetClause(queryPart);
	}
}
