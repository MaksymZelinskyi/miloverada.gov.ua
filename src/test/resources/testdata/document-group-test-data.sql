INSERT INTO public.document_group(id, name)
VALUES(1, 'group1')
ON CONFLICT (id) DO NOTHING;