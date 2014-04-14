	*) Bài toán phân cụm	
		: sử dụng thuật toán K-mean 
		: áp dụng với dữ liệu craw từ "baomoi.com"
		: biểu diễn các bài báo bằng vector thưa
	*) Dữ liệu
		: 1153 bài báo với 7 chủ đề lớn 			(thư mục: "tintuc")
			(chương trình lấy tất cả bài báo trong thư mục "tintuc", không quan tâm thư mục cha của bài báo
				việc để sẵn trong các chủ đề để có thể dễ dàng so sánh với output sau khi phân cụm)
		: bộ từ điển biểu diễn vector các bài báo 7007 từ	(file: "vectorDicWord.txt")
			(1001 từ xuất hiện nhiều nhất cho mỗi chủ đề lớn từ tập dữ liệu mẫu 420 bài báo)
	*) Thư viện
		: sử dụng công cụ tagger để đánh chỉ mục từ
		: sử dụng thư viện Gson để đọc các file dữ liệu
	*) Công thức tính toán:
		: Chỉ số các vector tính theo các chỉ số DFi, TFi
		: Độ tương tự giữa các bài báo tính theo công thức Cosim
	*) Output
		: thư mục "output" với k thư mục ứng với k cụm được phân lớp
		: nhận xét: 
			- độ chính xác không cao so với dữ liệu ban đầu đã được chia vào các chủ đề
			- giới hạn số vòng lặp phân cụm 1000 lần
