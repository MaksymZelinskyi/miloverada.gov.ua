alter table public.document_retrieval
    drop constraint document_retrieval_document_id_fkey;

alter table public.document
    drop constraint document_added_by_id_fkey;