-- Insert categories
INSERT INTO [recipeapp_dev].[dbo].[categories]
    ([id], [description], [name])
VALUES
    (NEWID(), N'Bữa cơm gia đình', 'Bữa cơm gia đình'),
    (NEWID(), N'Các món ngon với phở', 'Các món ngon với phở'),
    (NEWID(), N'Món ngon từ thịt heo', 'Món ngon từ thịt heo'),
    (NEWID(), N'Đồ ăn vặt', 'Đồ ăn vặt'),
    (NEWID(), N'Các món ngon với bún', 'Các món ngon với bún'),
    (NEWID(), N'Bữa tối ngon miệng, hấp dẫn', 'Bữa Tối'),
    (NEWID(), N'Món ngon từ cá', 'Món ngon từ cá'),
    (NEWID(), N'Bánh mì', 'Bánh mì'),
    (NEWID(), N'Bữa sáng ngon miệng, bổ dưỡng', 'Bữa Sáng'),
    (NEWID(), N'Các món lẩu', 'Các món lẩu'),
    (NEWID(), N'Món ngon từ thịt bò', 'Món ngon từ thịt bò'),
    (NEWID(), N'Món ngon từ gà', 'Món ngon từ gà'),
    (NEWID(), N'Đồ ăn chay', 'Đồ ăn chay');

-- Insert ingredients
INSERT INTO [recipeapp_dev].[dbo].[ingredients]
    ([id], [name])
VALUES
    (NEWID(), N'Bắp Cải'),
    (NEWID(), N'Bún'),
    (NEWID(), N'Cá Chép'),
    (NEWID(), N'Cải Thảo'),
    (NEWID(), N'Đỗ Đen'),
    (NEWID(), N'Dưa Chuột'),
    (NEWID(), N'Gạo'),
    (NEWID(), N'Thì Là'),
    (NEWID(), N'Thịt Bò'),
    (NEWID(), N'Tôm'),
    (NEWID(), N'Tôm Sú');

-- Insert Users
INSERT INTO [recipeapp_dev].[dbo].[users]
    ([id], [first_name], [last_name], [username], [email], [password])
VALUES
    ('84c2b2d8-cf53-4ddc-8cf8-bc96c83acc6d', N'Admin', N'Admin', 'admin', 'admin@domain.com', '$2a$10$KrR0wMdD86BNfbfbqr.a2e7OPqRXiLe6ZzREML53n47p9rHVII52.'),
    ('1c2c5b84-2bb8-4145-9031-28d5e5f4087c', N'User', N'User', 'user', 'user@domain.com', '$2a$10$KrR0wMdD86BNfbfbqr.a2e7OPqRXiLe6ZzREML53n47p9rHVII52.');

-- Insert roles
INSERT INTO [recipeapp_dev].[dbo].[roles]
    ([id], [name])
VALUES
    ('71f10ee7-de54-436a-b038-d2f93bdb26e1', 'ADMIN'),
    ('f2c876d6-6298-4c29-828c-d4675df6d455', 'USER');

-- Insert user_roles
INSERT INTO [recipeapp_dev].[dbo].[user_roles]
    ([user_id], [role_id])
VALUES
    ('84c2b2d8-cf53-4ddc-8cf8-bc96c83acc6d', '71f10ee7-de54-436a-b038-d2f93bdb26e1'),
    ('1c2c5b84-2bb8-4145-9031-28d5e5f4087c', 'f2c876d6-6298-4c29-828c-d4675df6d455');