{%- for type_ in ci.iter_types() %}
{%- match type_ %}

{%- when Type::External { module_path, name, namespace, kind, tagged } %}
{% include "ExternalTypeTemplate.h" %}

{%- else %}
{%- endmatch %}
{%- endfor %}
