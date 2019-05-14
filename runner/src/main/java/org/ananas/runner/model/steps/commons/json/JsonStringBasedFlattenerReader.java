package org.ananas.runner.model.steps.commons.json;

import com.github.wnameless.json.flattener.FlattenMode;
import com.github.wnameless.json.flattener.JsonFlattener;
import org.ananas.runner.model.steps.commons.ErrorHandler;
import org.ananas.runner.model.steps.commons.RowConverter;
import org.apache.beam.sdk.values.Row;

import java.io.Serializable;

public class JsonStringBasedFlattenerReader extends AbstractJsonFlattenerReader<String> implements Serializable {

	private static final long serialVersionUID = -891325732336493041L;

	public JsonStringBasedFlattenerReader(RowConverter converter, ErrorHandler errorHandler) {

		super(converter, errorHandler);
	}

	@Override
	public Row document2BeamRow(String doc) {
		try {
			return this.converter.convertMap(
					new JsonFlattener(doc).withFlattenMode(FlattenMode.KEEP_ARRAYS).flattenAsMap());
		} catch (Exception e) {
			this.errors.addError(e);
		}
		return null;
	}

}