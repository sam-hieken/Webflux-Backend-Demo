package com.lender.data.repository.converter;

import static com.lender.data.repository.pojo.LoanGrantType.ADMIN_CREATED;
import static com.lender.data.repository.pojo.LoanGrantType.AUTOMATIC;

import javax.persistence.AttributeConverter;

import com.lender.data.repository.pojo.LoanGrantType;

public class LoanGrantTypeConverter  implements AttributeConverter<LoanGrantType, Integer> {

	@Override
	public Integer convertToDatabaseColumn(LoanGrantType grantType) {
		if (grantType == null)
			return null;

		switch (grantType) {
		
		case AUTOMATIC:
			return 0;
			
		case ADMIN_CREATED:
			return 1;
		
		}
		
		throw new IllegalArgumentException(grantType + " not found.");
	}

	@Override
	public LoanGrantType convertToEntityAttribute(Integer id) {
		if (id == null)
			return null;

		switch (id) {
		
		case 0:
			return AUTOMATIC;

		case 1:
			return ADMIN_CREATED;
			
		}
		
		throw new IllegalArgumentException(id + " not supported.");
	}
}
