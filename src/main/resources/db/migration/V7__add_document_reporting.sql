alter table document_group add column if not exists group_order int;

create table if not exists public.document_retrieval
(
    id serial primary key,
    document_id integer references public.document(id),
    created_on        timestamp,
    "action" text
);

alter table public.document_retrieval
    owner to postgres;


alter table public.document
    add column if not exists added_by_id text;

alter table public.document
	add constraint document_added_by_id_fkey
        foreign key(added_by_id) references public.app_users(id);

