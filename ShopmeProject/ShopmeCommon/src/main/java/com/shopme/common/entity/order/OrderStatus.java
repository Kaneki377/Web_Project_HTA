package com.shopme.common.entity.order;

public enum OrderStatus {

	NEW {
		@Override
		public String defaultDescription() {
			return "Đơn hàng đã được tạo";
		}

	}, 

	CANCELLED {
		@Override
		public String defaultDescription() {
			return "Đơn hàng bị từ chối";
		}
	}, 

	PROCESSING {
		@Override
		public String defaultDescription() {
			return "Đang xử lý đơn hàng";
		}
	},

	PACKAGED {
		@Override
		public String defaultDescription() {
			return "Đơn hàng đã được đóng gói";
		}		
	}, 

	PICKED {
		@Override
		public String defaultDescription() {
			return "Đơn hàng đã được giao cho đơn vị vận chuyển";
		}		
	}, 

	SHIPPING {
		@Override
		public String defaultDescription() {
			return "Đơn hàng đang trên đường giao tới bạn";
		}		
	},

	DELIVERED {
		@Override
		public String defaultDescription() {
			return "Giao hàng thành công";
		}		
	}, 

	RETURNED {
		@Override
		public String defaultDescription() {
			return "Đơn hàng bị hoàn";
		}		
	}, 

	PAID {
		@Override
		public String defaultDescription() {
			return "Đã thanh toán qua Online Banking";
		}		
	}, 

	REFUNDED {
		@Override
		public String defaultDescription() {
			return "Hàng đã được hoàn trả thành công";
		}		
	},
	
	RETURN_REQUESTED {
		@Override
		public String defaultDescription() {
			return "Khách yêu cầu trả hàng";
		}		
	};

	public abstract String defaultDescription();
}
