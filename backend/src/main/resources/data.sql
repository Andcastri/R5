-- Insertar algunos planes de ejemplo
INSERT INTO home_plans (plan_name, internet_speed, television, decoder, local_phone, price, tariff_code, campaign, image_url, description, last_updated)
VALUES 
('Plan Básico', '50 Mbps', 'TV HD', 'Decodificador HD', 'Línea básica', 49.99, 'BASIC001', 'Verano 2024', '/api/images/default.jpg', 'Plan básico para el hogar', CURRENT_TIMESTAMP),
('Plan Premium', '200 Mbps', 'TV 4K', 'Decodificador 4K', 'Línea ilimitada', 89.99, 'PREM001', 'Verano 2024', '/api/images/default.jpg', 'Plan premium con máxima velocidad', CURRENT_TIMESTAMP),
('Plan Familiar', '100 Mbps', 'TV HD+', 'Decodificador HD+', 'Línea familiar', 69.99, 'FAM001', 'Primavera 2024', '/api/images/default.jpg', 'Plan ideal para familias', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING; 