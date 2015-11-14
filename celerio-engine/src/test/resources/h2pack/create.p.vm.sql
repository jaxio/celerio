$output.sql("h2/generated", "tables.sql")##
DROP ALL objects;

#foreach($table in $metadata.tables)
CREATE TABLE $table.name (
#foreach($column in $table.columns)
	#commaStart($velocityCount)$column.h2
#end
);
#foreach($indexName in $table.indexHoldersByName.keySet())
#if (!$indexName.startsWith("PRIMARY_KEY_"))
#set($indexHolder = $table.getIndexHolderByName($indexName))
CREATE#if ($indexHolder.isUnique()) UNIQUE#end INDEX IF NOT EXISTS $indexHolder.name
	ON $table.name (#foreach($index in $indexHolder.indexes)#commaStart($velocityCount)$index.columnName#end);
#end
#end
#if ($table.hasPk())
CREATE PRIMARY KEY
	ON $table.name (#foreach($pkName in $table.primaryKeys)#commaStart($velocityCount)$pkName#end);
#end

#end

#foreach($table in $metadata.tables)
#foreach($foreignKeyName in $table.getForeignKeysByName().keySet())
#set($foreignKey = $table.getForeignKeyByName($foreignKeyName))
ALTER TABLE $table.name
	ADD CONSTRAINT $foreignKeyName
		FOREIGN KEY (#foreach($importedKey in $foreignKey.importedKeys)#commaStart($velocityCount)$importedKey.fkColumnName#end)
			REFERENCES $foreignKey.pkTableName (#foreach($importedKey in $foreignKey.importedKeys)#commaStart($velocityCount)$importedKey.pkColumnName#end);
#end
#end
