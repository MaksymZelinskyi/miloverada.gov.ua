INSERT INTO public.app_users(id, first_name, last_name, email, avatar_url)
VALUES('user', 'John', 'Doe', 'user@email.com', 'http://www.example.com')
ON CONFLICT(id) DO NOTHING;