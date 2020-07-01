
#include "pybind11/embed.h"
#include "pybind11/numpy.h"
#include "pybind11/stl.h"

#include "datapipeline.hh"

using namespace std;
namespace py = pybind11;

DataPipeline::DataPipeline(const string &config_file)
{
  // TODO: tidy up these variable names
  pd = py::module::import("pandas");

  // TODO: StandardAPI needs here
  // SimpleNetworkSimAPI = py::module::import(
  //                           "data_pipeline_api.simple_network_sim_api")
  //                           .attr("SimpleNetworkSimAPI");

  // api = SimpleNetworkSimAPI(config_file);

  py::object StandardAPIClass = py::module::import("data_pipeline_api.standard_api").attr("StandardAPI");

  StandardAPI = StandardAPIClass(config_file);
  ObjectFileAPI = py::module::import("data_pipeline_api.standard_api").attr("object_file");
}

double DataPipeline::read_estimate(string data_product, const string &component)
{
  double est = py::float_(StandardAPI.attr("read_estimate")(data_product));
  // TODO: what about component?
  return est;
}

//Distribution DataPipeline::read_distribution(const string &data_product, const string &component);

double DataPipeline::read_sample(const string *data_product, const string &component)
{
  double est = py::float_(StandardAPI.attr("read_sample")(data_product));
  return est;
}

//void DataPipeline::write_estimate(const string &data_product, const string &component, double estimate);
//void DataPipeline::write_distribution(const string &data_product, const string &component,
//                        const Distribution &d);
//void DataPipeline::write_sample(const string &data_product, const string &component, const vector<double> &samples);

void DataPipeline::write_array(const string &data_product, const string &component, const Array &da)
{
  py::object group = ObjectFileAPI.attr("get_write_group")(data_product, component);
  da.encode(group);
}

template <typename DT>
typename std::shared_ptr<ArrayT<DT>> DataPipeline::read_array_T(const string &data_product, const string &component)
{
  /// NOTE:  file open should be done by python get_read_group(), to have access check
  py::object group = ObjectFileAPI.attr("get_read_group")(data_product, component);

  typename ArrayT<DT>::Ptr ap = ArrayT<DT>::decode(group);

  // no need to close dataset? group
  return ap;
}

typename std::shared_ptr<Array> DataPipeline::read_array(const string &data_product, const string &component)
{
  py::object group = ObjectFileAPI.attr("get_read_group")(data_product, component);
  return DataDecoder::decode_array(group);
}

Table DataPipeline::read_table(const string &data_product, const string &component)
{

  Table table;
  /// TODO: StandardAPI.attr("read_table") may be not the appliable
  py::object dataframe = StandardAPI.attr("read_table")(data_product, component + "/table");

  vector<string> colnames = dataframe.attr("columns").attr("tolist")().cast<vector<string>>();

  for (const auto &colname : colnames)
  {

    string dtype = py::str(dataframe.attr("dtypes").attr(colname.c_str()));

    if (dtype == "float64")
    {
      vector<double> values = dataframe[colname.c_str()].attr("tolist")().cast<vector<double>>();
      table.add_column<double>(colname, values);
    }
    else if (dtype == "int64")
    {
      vector<int64_t> values = dataframe[colname.c_str()].attr("tolist")().cast<vector<int64_t>>();
      table.add_column<int64_t>(colname, values);
    }
    else if (dtype == "bool")
    {
      vector<bool> values = dataframe[colname.c_str()].attr("tolist")().cast<vector<bool>>();
      table.add_column<bool>(colname, values);
    }
    else if (dtype == "string" || dtype == "object") // tested working
    {
      vector<std::string> values = dataframe[colname.c_str()].attr("tolist")().cast<vector<std::string>>();
      table.add_column<std::string>(colname, values);
    }
    else
    {
      cout << "WARNING: Converting column " << colname << " from unsupported type " << dtype << " to string" << endl;
      vector<string> values;
      py::list l = dataframe[colname.c_str()].attr("tolist")();
      for (const auto &it : l)
      {
        values.push_back(py::str(it));
      }
      table.add_column<string>(colname, values);
    }
  }

  py::object group = ObjectFileAPI.attr("get_read_group")(data_product, component);
  /// TODO: Table has not define fields to save these meta data
  //py::str _title = group.attr("__getitem__")(py::str("row_title"));
  //py::list _names = group.attr("__getitem__")(py::str("row_names"));
  if (group.attr("contains")(py::str("column_units")))
  {
    py::list _units = group.attr("__getitem__")(py::str("column_units"));
    table.set_column_units(_units.cast<std::vector<std::string>>());
  }

  return table;
}

void DataPipeline::write_table(const string &data_product, const string &component,
                               const Table &table)
{
  map<string, py::array> _map; // pybind automatically recognises a map as a dict

  for (const auto &colname : table.get_column_names())
  {
    string dtype = table.get_column_type(colname);
    py::list l;
    if (dtype == "float64")
    {
      l = py::cast(table.get_column<double>(colname));
    }
    else if (dtype == "int64")
    {
      l = py::cast(table.get_column<int64_t>(colname));
    }
    else if (dtype == "bool")
    {
      l = py::cast(table.get_column<bool>(colname));
    }
    else if (dtype == "string" || dtype == "object")
    {
      l = py::cast(table.get_column<std::string>(colname));
    }
    else
    {
      cout << "WARNING: skip column " << colname << " from unsupported type " << dtype << endl;
    }
    _map[colname] = l;
  }
  py::object _df = pd.attr("DataFrame")(_map);
  /// ObjectFileAPI has also the lower level API, write_table()
  StandardAPI.attr("write_table")(data_product, component + "/table", _df);

  /// BUG or improper usage of API,   can not write to this file, file exists
  /// ObjectFileAPI may be not desired to be use directly?
  py::object group = ObjectFileAPI.attr("get_write_group")(data_product, component);
  if (table.get_column_units().size() > 0)
  {
    py::list _units = py::cast(table.get_column_units());
    group.attr("__setitem__")(py::str("column_units"), _units);
  }
}