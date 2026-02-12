INSERT INTO public.document_group(id, name)
VALUES(1, 'group1')
ON CONFLICT (id) DO NOTHING;

INSERT INTO public.document(id, title, name, hash_code, document_group_id)
VALUES(1, 'document1', 'document1', 32, 1)
ON CONFLICT (id) DO NOTHING;