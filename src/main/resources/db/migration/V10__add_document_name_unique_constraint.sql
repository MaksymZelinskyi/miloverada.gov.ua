DELETE FROM document
WHERE id NOT IN (
    SELECT MIN(id)
    FROM document
    GROUP BY name
);

ALTER TABLE document ADD CONSTRAINT uq_document_name UNIQUE (name);