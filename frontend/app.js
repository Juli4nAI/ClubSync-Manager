// =========================================================================
// 🚀 URL DEL BACKEND (RAILWAY)
// =========================================================================
const API_BASE_URL = 'https://clubsync-manager.onrender.com';


let actividadesArray = []; 
let modalHorariosObj;
let modalEditarObj;
let modalParticipantesObj;
let modalPersonaObj;
let modalPagoObj;
let modalHistorialObj;
let radarDePago;

document.addEventListener('DOMContentLoaded', function() {
    modalHorariosObj = new bootstrap.Modal(document.getElementById('modalHorarios'));
    modalEditarObj = new bootstrap.Modal(document.getElementById('modalEditarActividad'));
    modalParticipantesObj = new bootstrap.Modal(document.getElementById('modalParticipantes'));
    modalPagoObj = new bootstrap.Modal(document.getElementById('modalPago')); 
    modalHistorialObj = new bootstrap.Modal(document.getElementById('modalHistorialPagos'));
    
    const modalP = document.getElementById('modalPersona');
    if (modalP) modalPersonaObj = new bootstrap.Modal(modalP);

    cargarInstalacionesParaEdicion();

    const formEdit = document.getElementById('formEditarActividad');
    if(formEdit) {
        formEdit.addEventListener('submit', function(e) {
            e.preventDefault(); 
            guardarEdicionActividad();
        });
    }

    document.getElementById('modalVistaQR').addEventListener('hidden.bs.modal', function () {
        if (radarDePago) {
            clearInterval(radarDePago); 
        }
    });
});


function toggleMenu() {
    document.getElementById('sidebar').classList.toggle('mostrar');
    document.getElementById('overlay').classList.toggle('mostrar');
}

function mostrarVista(idVista, elementoMenu) {
    document.querySelectorAll('.vista').forEach(v => v.classList.remove('activa'));
    document.querySelectorAll('.sidebar a').forEach(a => a.classList.remove('activo'));
    
    document.getElementById(idVista).classList.add('activa');
    elementoMenu.classList.add('activo');

    if (window.innerWidth <= 768) {
        document.getElementById('sidebar').classList.remove('mostrar');
        document.getElementById('overlay').classList.remove('mostrar');
    }

    if (idVista === 'socios') cargarPersonas(); 
    if (idVista === 'actividades') cargarActividades();
    if (idVista === 'instalaciones') cargarInstalaciones();
}


function cargarPersonas() {
    const tbody = document.getElementById('tabla-personas');
    if (!tbody) return;

    const selectRol = document.getElementById('filtro-rol');
    const selectEstado = document.getElementById('filtro-estado-sistema');
    
    const filtroRol = selectRol ? selectRol.value : 'TODOS';
    const filtroEstadoSis = selectEstado ? selectEstado.value : 'AMBOS';

    tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4"><div class="spinner-border spinner-border-sm text-primary"></div> Cargando padrón...</td></tr>';

    Promise.all([
        fetch(`${API_BASE_URL}/api/personas`).then(res => res.ok ? res.json() : []),
        fetch(`${API_BASE_URL}/api/socios`).then(res => res.ok ? res.json() : [])
    ])
    .then(([listaPersonas, listaSocios]) => {
        tbody.innerHTML = '';

        const listaFiltrada = listaPersonas.filter(per => {
            const montoDeuda = per.deuda ? parseFloat(per.deuda) : 0;
            const estadoPersona = per.estado ? per.estado.toUpperCase().trim() : 'ACTIVO';
            
            const datosSocio = listaSocios.find(s => s.persona && s.persona.id === per.id);
            const esSocioActivo = !!datosSocio && datosSocio.estado.toUpperCase().trim() === 'ACTIVO';

            let pasaFiltroEstado = true;
            if (filtroEstadoSis === 'ACTIVO') pasaFiltroEstado = (estadoPersona === 'ACTIVO');
            if (filtroEstadoSis === 'INACTIVO') pasaFiltroEstado = (estadoPersona === 'INACTIVO');

            let pasaFiltroRol = true;
            if (filtroRol === 'SOCIOS') pasaFiltroRol = esSocioActivo;
            else if (filtroRol === 'PERSONAS') pasaFiltroRol = !esSocioActivo;
            else if (filtroRol === 'DEUDORES') pasaFiltroRol = (montoDeuda > 0);

            return pasaFiltroEstado && pasaFiltroRol;
        });

        if (listaFiltrada.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">No se encontraron resultados con estos filtros.</td></tr>';
            return;
        }

        listaFiltrada.forEach(per => {
            const nombre = per.apynom || 'Sin nombre';
            const dni = per.dni || 'S/D';
            const telefono = per.celular || '-'; 
            const mail = per.email || '-';       
            const montoDeuda = per.deuda ? parseFloat(per.deuda) : 0;
            const estadoPersona = per.estado ? per.estado.toUpperCase().trim() : 'ACTIVO';
            const esPersonaBaja = estadoPersona === 'INACTIVO';

            const datosSocio = listaSocios.find(s => s.persona && s.persona.id === per.id);
            const esSocio = !!datosSocio;
            const estadoSocio = esSocio && datosSocio.estado ? datosSocio.estado.toUpperCase().trim() : 'ACTIVO';
            const esSocioActivo = esSocio && estadoSocio === 'ACTIVO';
            const esSocioBaja = esSocio && !esSocioActivo;

            let badgeDeuda = montoDeuda > 0 
                ? `<span class="badge bg-danger shadow-sm px-2 py-1"><i class="bi bi-exclamation-triangle-fill"></i> $${montoDeuda.toFixed(2)}</span>`
                : `<span class="badge bg-light text-success border border-success px-2 py-1"><i class="bi bi-check-circle-fill"></i> Al día</span>`;

            let badgeRol = '';
            if (esSocioActivo) {
                badgeRol = '<span class="badge bg-success shadow-sm mb-1"><i class="bi bi-star-fill"></i> SOCIO</span>';
            } else if (esSocioBaja) {
                badgeRol = `<span class="badge bg-secondary opacity-75 shadow-sm mb-1">NO SOCIO</span><span class="badge bg-warning text-dark shadow-sm mb-1 ms-1" title="Fue socio anteriormente"><i class="bi bi-clock-history"></i> EX SOCIO</span>`;
            } else {
                badgeRol = '<span class="badge bg-secondary opacity-50 mb-1">NO SOCIO</span>';
            }

            let badgeEstadoPersona = esPersonaBaja ? '<span class="badge rounded-pill border border-danger text-danger mt-1">Baja Sistema</span>' : '';

            let botonesHTML = '';
            let claseFila = esPersonaBaja ? 'table-light text-muted opacity-75' : '';

            let btnCobrar = montoDeuda > 0 
                ? `<button class="btn btn-sm btn-outline-success me-1 shadow-sm" onclick="abrirModalPago(${per.id}, '${nombre}', ${montoDeuda})" title="Registrar Pago">
                       <i class="bi bi-cash-coin"></i>
                   </button>` 
                : '';

            let btnHistorial = `<button class="btn btn-sm btn-outline-info me-1 shadow-sm" onclick="verHistorialPagos(${per.id}, '${nombre}')" title="Ver Historial de Pagos">
                                    <i class="bi bi-clock-history"></i>
                                </button>`;

            if (esPersonaBaja) {
                botonesHTML = `<button class="btn btn-sm btn-outline-success fw-bold shadow-sm" onclick="reactivarPersona(${per.id})" title="Restaurar Persona"><i class="bi bi-arrow-counterclockwise"></i> Restaurar</button>`;
            } else {
                let btnAccionSocio = '';
                if (!esSocio) {
                    btnAccionSocio = `<button class="btn btn-sm btn-outline-success me-1 shadow-sm" onclick="hacerSocio(${per.id})" title="Registrar como Socio"><i class="bi bi-person-up"></i></button>`;
                } else if (esSocioActivo) {
                    btnAccionSocio = `<button class="btn btn-sm btn-outline-secondary me-1 shadow-sm" onclick="darDeBajaSocio(${datosSocio.id})" title="Dar de baja como Socio"><i class="bi bi-person-down"></i></button>`;
                } else if (esSocioBaja) {
                    btnAccionSocio = `<button class="btn btn-sm btn-outline-success me-1 shadow-sm" onclick="reasociarSocio(${datosSocio.id}, ${montoDeuda})" title="Volver a asociar"><i class="bi bi-person-check-fill"></i></button>`;
                }

                botonesHTML = `
                    ${btnHistorial}
                    ${btnAccionSocio}
                    ${btnCobrar}

                    <button class="btn btn-sm btn-outline-warning me-1 shadow-sm" onclick="abrirModalEditarPersona(${per.id})" title="Editar Datos"><i class="bi bi-pencil"></i></button>
                    <button class="btn btn-sm btn-outline-danger shadow-sm" onclick="borrarPersona(${per.id})" title="Eliminar del Sistema"><i class="bi bi-trash"></i></button>
                `;
            }

            tbody.innerHTML += `
                <tr class="${claseFila}">
                    <td class="ps-4 align-middle fw-bold text-primary">${nombre}</td>
                    <td class="align-middle fw-bold text-secondary"><i class="bi bi-card-heading"></i> ${dni}</td>
                    <td class="align-middle small">
                        <div><i class="bi bi-telephone"></i> ${telefono}</div>
                        <div><i class="bi bi-envelope"></i> ${mail}</div>
                    </td>
                    <td class="align-middle">
                        <div class="d-flex flex-column align-items-start">
                            ${badgeRol}
                            ${badgeEstadoPersona}
                        </div>
                    </td>
                    <td class="align-middle">${badgeDeuda}</td>
                    <td class="text-end pe-4 align-middle">${botonesHTML}</td>
                </tr>
            `;
        });
    })
    .catch(error => {
        console.error("Error al cargar personas:", error);
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-danger py-4">Error al cargar la base de datos.</td></tr>';
    });
}

function abrirModalNuevaPersona() {
    document.getElementById('formPersona').reset();
    document.getElementById('personaId').value = '';
    document.getElementById('tituloModalPersona').innerHTML = '<i class="bi bi-person-plus-fill"></i> Registrar Persona';
    modalPersonaObj.show();
}

function guardarPersona() {
    const id = document.getElementById('personaId').value;
    const datosPersona = {
        apynom: document.getElementById('personaNombre').value,
        dni: document.getElementById('personaDni').value,
        celular: document.getElementById('personaTelefono').value,
        email: document.getElementById('personaMail').value        
    };

    if (!datosPersona.apynom || !datosPersona.dni) {
        alert("El Nombre y el DNI son obligatorios.");
        return;
    }

    const metodo = id ? 'PUT' : 'POST';
    const urlDestino = id ? `${API_BASE_URL}/api/personas/${id}` : `${API_BASE_URL}/api/personas`;

    fetch(urlDestino, {
        method: metodo,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(datosPersona)
    })
    .then(async respuesta => {
        if (respuesta.ok) {
            alert(id ? "¡Datos actualizados con éxito!" : "¡Persona registrada al sistema!");
            modalPersonaObj.hide();
            cargarPersonas(); 
        } else {
            const error = await respuesta.text();
            alert("Error al guardar: " + error);
        }
    })
    .catch(error => console.error('Error:', error));
}

function abrirModalEditarPersona(id) {
    fetch(`${API_BASE_URL}/api/personas/${id}`)
        .then(res => {
            if (!res.ok) throw new Error("No se pudo cargar la persona");
            return res.json();
        })
        .then(persona => {
            document.getElementById('personaId').value = persona.id;
            document.getElementById('personaNombre').value = persona.apynom;
            document.getElementById('personaDni').value = persona.dni;
            document.getElementById('personaTelefono').value = persona.celular || '';
            document.getElementById('personaMail').value = persona.email || '';
            document.getElementById('tituloModalPersona').innerHTML = '<i class="bi bi-pencil-square"></i> Editar Persona';
            modalPersonaObj.show();
        })
        .catch(error => {
            console.error(error);
            alert("Error al intentar cargar los datos para editar.");
        });
}

async function borrarPersona(idPersona) {
    try {
        const resCheck = await fetch(`${API_BASE_URL}/api/personas/${idPersona}/es-responsable`);
        const esResponsable = await resCheck.json(); 

        let mensaje = "¿Estás seguro de que querés dar de baja a esta persona del sistema?";
        if (esResponsable) {
            mensaje = "⚠️ ATENCIÓN: Esta persona está asignada como RESPONSABLE de una o más actividades/eventos. \n\nSi la das de baja, será removida y las actividades quedarán sin profesor. ¿Deseas continuar de todos modos?";
        }

        if (confirm(mensaje)) {
            const respuesta = await fetch(`${API_BASE_URL}/api/personas/${idPersona}/baja`, { method: 'PUT' });
            if (respuesta.ok) {
                alert("¡Persona dada de baja con éxito!");
                cargarPersonas(); 
            } else {
                const error = await respuesta.text();
                alert("Hubo un problema al dar de baja: " + error);
            }
        }
    } catch (error) {
        console.error('Error:', error);
        alert("Ocurrió un error al verificar el estado de la persona.");
    }
}

function reactivarPersona(idPersona) {
    if (confirm("¿Querés volver a activar a esta persona en el sistema?")) {
        fetch(`${API_BASE_URL}/api/personas/${idPersona}/reactivar`, { method: 'PUT' })
        .then(async respuesta => {
            if (respuesta.ok) {
                alert("¡Persona restaurada con éxito!");
                cargarPersonas();
            } else {
                const error = await respuesta.text();
                alert("No se pudo restaurar. Detalle: " + error);
            }
        })
        .catch(error => console.error('Error de red:', error));
    }
}


function hacerSocio(idPersona) {
    if(!confirm("¿Confirmás que querés registrar a esta persona como Socio activo?")) return;
    fetch(`${API_BASE_URL}/api/socios/promocionar/${idPersona}`, { method: 'POST' })
    .then(res => {
        if(res.ok) {
            alert("¡Ahora es un Socio oficial!");
            cargarPersonas(); 
        } else {
            res.text().then(msg => alert(msg));
        }
    });
}

function darDeBajaSocio(idSocio) {
    if (confirm("¿Estás seguro de que querés dar de baja a este socio? Pasará a figurar como EX SOCIO.")) {
        fetch(`${API_BASE_URL}/api/socios/${idSocio}/baja`, { method: 'PUT' })
        .then(async res => {
            if (res.ok) {
                alert("¡Socio dado de baja con éxito!");
                cargarPersonas(); 
            } else {
                const error = await res.text();
                alert("Error al intentar dar de baja: " + error);
            }
        })
        .catch(err => console.error("Error de red:", err));
    }
}

function reasociarSocio(idSocio, montoDeuda) {
    if (montoDeuda > 0) {
        alert(`❌ OPERACIÓN DENEGADA:\nEsta persona mantiene una deuda de $${montoDeuda.toFixed(2)}.\nDebe regularizar su situación en Tesorería antes de volver a ser socio activo.`);
        return;
    }

    if (confirm("¿Querés volver a dar de alta a esta persona como Socio activo? Su fecha de alta se actualizará a hoy.")) {
        fetch(`${API_BASE_URL}/api/socios/${idSocio}/reasociar`, { method: 'PUT' })
        .then(async res => {
            if (res.ok) {
                alert("¡Persona reasociada con éxito!");
                cargarPersonas(); 
            } else {
                const error = await res.text();
                alert("Error al reasociar: " + error);
            }
        })
        .catch(err => console.error("Error de red:", err));
    }
}


function cargarInstalaciones() {
    const grid = document.getElementById('grid-instalaciones');
    grid.innerHTML = '<p class="text-center w-100">Cargando instalaciones...</p>';
    
    fetch(`${API_BASE_URL}/api/instalaciones`)
        .then(res => res.json())
        .then(datos => {
            grid.innerHTML = '';
            if(datos.length === 0) { grid.innerHTML = '<p class="text-muted">No hay instalaciones registradas.</p>'; return; }
            
            datos.forEach(inst => {
                const estado = inst.estado ? inst.estado.toUpperCase() : 'DISPONIBLE';
                const nombre = inst.nombre || `Instalación #${inst.id}`;
                const capacidad = inst.capacidad || 'No definida';
                const descripcion = inst.descripcion || 'Sin descripción';
                const textoAncho = inst.ancho ? `${inst.ancho}m` : 'N/A';
                const textoLargo = inst.largo ? `${inst.largo}m` : 'N/A';
                
                let colorBadge = 'bg-success';
                let colorBorde = 'border-success';
                let estiloOpaco = ''; 

                if (estado === 'MANTENIMIENTO') {
                    colorBadge = 'bg-warning text-dark';
                    colorBorde = 'border-warning';
                } else if (estado === 'INACTIVO') {
                    colorBadge = 'bg-secondary'; 
                    colorBorde = 'border-secondary';
                    estiloOpaco = 'opacity-75 bg-light'; 
                }

                grid.innerHTML += `
                    <div class="col-md-4 mb-3">
                        <div class="card h-100 shadow-sm border-0 ${colorBorde} border-start border-5 ${estiloOpaco}">
                            <div class="card-body">
                                <h5 class="card-title fw-bold">${nombre}</h5>
                                <p class="text-muted mb-2"><i class="bi bi-people"></i> Capacidad: ${capacidad}</p>
                                <p class="card-text">${descripcion}</p>
                                <p class="mb-2"><i class="bi bi-arrows-fullscreen"></i> Dimensiones: ${textoAncho} x ${textoLargo}</p>
                                <span class="badge ${colorBadge} mb-3">${estado}</span>
                                <hr class="mt-0">
                                <div class="d-flex justify-content-end gap-2">
                                    <button class="btn btn-sm btn-outline-primary" onclick="abrirModalEditar(${inst.id}, '${nombre}', ${capacidad}, '${estado}', '${descripcion}', ${inst.ancho || 0}, ${inst.largo || 0})">
                                        <i class="bi bi-pencil"></i>
                                    </button>
                                    ${estado !== 'INACTIVO' ? `
                                        <button class="btn btn-sm btn-outline-danger" onclick="eliminarInstalacion(${inst.id})">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    ` : ''}
                                </div>
                            </div>
                        </div>
                    </div>
                `;
            });
        }).catch(e => grid.innerHTML = '<p class="text-danger text-center w-100">Error al cargar la base de datos.</p>');
}

function abrirModalNuevaInstalacion() {
    document.getElementById('form-instalacion').reset(); 
    const modal = new bootstrap.Modal(document.getElementById('modalNuevaInstalacion'));
    modal.show();
}

function guardarInstalacion() {
    const nombre = document.getElementById('inst-nombre').value;
    const capacidad = parseInt(document.getElementById('inst-capacidad').value);
    const descripcion = document.getElementById('inst-descripcion').value;
    const ancho = parseFloat(document.getElementById('inst-ancho').value);
    const largo = parseFloat(document.getElementById('inst-largo').value);
    const estado = document.getElementById('inst-estado') ? document.getElementById('inst-estado').value : 'DISPONIBLE';

    if (!nombre || isNaN(capacidad)) {
        alert('Por favor, completá el nombre y la capacidad con un número válido.');
        return;
    }

    const nuevaInstalacion = { nombre, capacidad, descripcion, ancho, largo, estado };

    fetch(`${API_BASE_URL}/api/instalaciones`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(nuevaInstalacion)
    })
    .then(respuesta => {
        if (respuesta.ok) {
            const modalEl = document.getElementById('modalNuevaInstalacion');
            bootstrap.Modal.getInstance(modalEl).hide();
            cargarInstalaciones();
        } else {
            alert('Error al guardar. Revisá la consola de Spring Boot.');
        }
    })
    .catch(error => console.error('Error de conexión:', error));
}

function eliminarInstalacion(id) {
    if (confirm("¿Estás seguro de que querés eliminar esta instalación?")) {
        fetch(`${API_BASE_URL}/api/instalaciones/${id}`, { method: 'DELETE' })
        .then(respuesta => {
            if (respuesta.ok) cargarInstalaciones();
            else alert("Hubo un error al intentar eliminar. Tiene actividades asociadas");
        })
        .catch(error => console.error("Error:", error));
    }
}

function abrirModalEditar(id, nombre, capacidad, estado, descripcion, ancho, largo) {
    document.getElementById('edit-inst-id').value = id;
    document.getElementById('edit-inst-nombre').value = nombre;
    document.getElementById('edit-inst-capacidad').value = capacidad;
    document.getElementById('edit-inst-estado').value = estado; 
    document.getElementById('edit-inst-descripcion').value = descripcion;
    document.getElementById('edit-inst-ancho').value = ancho;
    document.getElementById('edit-inst-largo').value = largo;

    const modal = new bootstrap.Modal(document.getElementById('modalEditarInstalacion'));
    modal.show();
}

function guardarEdicionInstalacion() {
    const id = document.getElementById('edit-inst-id').value; 
    const instalacionEditada = {
        nombre: document.getElementById('edit-inst-nombre').value,
        capacidad: parseInt(document.getElementById('edit-inst-capacidad').value),
        descripcion: document.getElementById('edit-inst-descripcion').value,
        ancho: parseFloat(document.getElementById('edit-inst-ancho').value),
        largo: parseFloat(document.getElementById('edit-inst-largo').value),
        estado: document.getElementById('edit-inst-estado') ? document.getElementById('edit-inst-estado').value : 'DISPONIBLE'
    };

    fetch(`${API_BASE_URL}/api/instalaciones/${id}`, {
        method: 'PUT', 
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(instalacionEditada)
    })
    .then(respuesta => {
        if (respuesta.ok) {
            const modalEl = document.getElementById('modalEditarInstalacion');
            bootstrap.Modal.getInstance(modalEl).hide();
            cargarInstalaciones();
        } else {
            alert('Error al intentar actualizar la instalación.');
        }
    })
    .catch(error => console.error('Error:', error));
}


function cargarOpcionesInstalaciones(selectId, instalacionSeleccionadaId = null) {
    const select = document.getElementById(selectId);
    select.innerHTML = '<option value="">Cargando instalaciones...</option>';

    fetch(`${API_BASE_URL}/api/instalaciones`)
        .then(res => res.json())
        .then(datos => {
            select.innerHTML = '<option value="">Seleccione una instalación...</option>';
            datos.forEach(inst => {
                const estado = inst.estado ? inst.estado.toUpperCase() : 'DISPONIBLE';
                if (estado === 'INACTIVO') return;
                const selected = (instalacionSeleccionadaId === inst.id) ? 'selected' : '';
                select.innerHTML += `<option value="${inst.id}" ${selected}>${inst.nombre} (Cap: ${inst.capacidad})</option>`;
            });
        })
        .catch(error => {
            select.innerHTML = '<option value="">Error al cargar instalaciones</option>';
        });
}

function cargarInstalacionesParaEdicion() {
    fetch(`${API_BASE_URL}/api/instalaciones`)
        .then(response => response.json())
        .then(instalaciones => {
            const select = document.getElementById('editInstalacionActividad');
            if(!select) return;
            instalaciones.forEach(inst => {
                const option = document.createElement('option');
                option.value = inst.id;
                option.textContent = `${inst.nombre} (Cap: ${inst.capacidad})`;
                select.appendChild(option);
            });
        });
}

function cargarOpcionesResponsables(selectId, responsableSeleccionadoId = null) {
    const select = document.getElementById(selectId);
    select.innerHTML = '<option value="">Cargando responsables...</option>';

    fetch(`${API_BASE_URL}/api/socios`)
        .then(res => res.json())
        .then(datos => {
            select.innerHTML = '<option value="">Seleccione un responsable...</option>';
            datos.forEach(socio => {
                const nombre = socio.nombreCompleto || (socio.persona ? socio.persona.apynom : `Socio #${socio.id}`);
                const selected = (responsableSeleccionadoId === socio.id) ? 'selected' : '';
                select.innerHTML += `<option value="${socio.id}" ${selected}>${nombre}</option>`;
            });
        })
        .catch(error => {
            select.innerHTML = '<option value="">Error al cargar responsables</option>';
        });
}

function cargarActividades() {
    const tbody = document.getElementById('tabla-actividades');
    tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted"><div class="spinner-border spinner-border-sm text-primary"></div> Cargando...</td></tr>';
    
    Promise.all([
        fetch(`${API_BASE_URL}/api/actividades`).then(res => res.ok ? res.json() : []),
        fetch(`${API_BASE_URL}/api/eventos`).then(res => res.ok ? res.json() : [])
    ])
    .then(([listaActividades, listaEventos]) => {
        const actividadesMarcadas = listaActividades.map(act => ({ ...act, tipoRegistro: 'ACTIVIDAD' }));
        const eventosMarcados = listaEventos.map(ev => ({ ...ev, tipoRegistro: 'EVENTO' }));
        
        actividadesArray = [...actividadesMarcadas, ...eventosMarcados];
        tbody.innerHTML = '';
        
        if (actividadesArray.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No hay registros cargados.</td></tr>';
            return;
        }
        
        actividadesArray.forEach(act => {
            const nombre = act.nombre || 'Sin nombre';
            const monto = act.monto || 0;
            const instNombre = act.instalacion ? act.instalacion.nombre : 'Sin asignar';
            const respNombre = act.responsable && act.responsable.apynom 
                                ? act.responsable.apynom 
                                : (act.responsable ? `Socio #${act.responsable.id}` : 'Sin responsable');

            const badgeTipo = act.tipoRegistro === 'EVENTO' 
                ? '<span class="badge bg-warning text-dark ms-2 shadow-sm" style="font-size: 0.7em;">EVENTO</span>'
                : '<span class="badge bg-primary ms-2 shadow-sm" style="font-size: 0.7em;">ACTIVIDAD</span>';                 

            let htmlHorarios = '<span class="text-muted small">Sin horarios</span>';
            if (act.horarios && act.horarios.length > 0) {
                const mapaDiasTabla = {
                    "0": "DOM", "1": "LUN", "2": "MAR", "3": "MIE", "4": "JUE", "5": "VIE", "6": "SAB", "7": "DOM",
                    "MONDAY": "LUN", "TUESDAY": "MAR", "WEDNESDAY": "MIE", "THURSDAY": "JUE", "FRIDAY": "VIE", "SATURDAY": "SAB", "SUNDAY": "DOM",
                    "LUNES": "LUN", "MARTES": "MAR", "MIERCOLES": "MIE", "JUEVES": "JUE", "VIERNES": "VIE", "SABADO": "SAB", "DOMINGO": "DOM"
                };

                htmlHorarios = act.horarios.map(h => {
                    const inicio = h.horaInicio.substring(0, 5);
                    const fin = h.horaFin.substring(0, 5);
                    const valorDia = String(h.diaSemana).toUpperCase();
                    const nombreDia = mapaDiasTabla[valorDia] || h.diaSemana;

                    return `<span class="badge bg-info text-dark mb-1 me-1 shadow-sm" style="font-size: 0.85rem; padding: 0.4em 0.7em;">
                                ${nombreDia}: ${inicio} - ${fin}
                            </span>`;
                }).join('');
            }

            const estadoAct = act.estado ? act.estado.toUpperCase().trim() : 'ACTIVO';
            const esInactiva = estadoAct === 'INACTIVO';
            
            const badgeEstado = esInactiva 
                ? '<span class="badge rounded-pill bg-danger">Inactivo</span>' 
                : '<span class="badge rounded-pill bg-success">Activo</span>';

            let botonesHTML = '';
            let claseFila = esInactiva ? 'table-light text-muted opacity-75' : '';
            
            if (esInactiva) {
                botonesHTML = `
                    <button class="btn btn-sm btn-outline-success shadow-sm fw-bold" onclick="reactivarActividad(${act.id}, '${act.tipoRegistro}')" title="Reactivar">
                        <i class="bi bi-arrow-counterclockwise me-1"></i> Restaurar
                    </button>
                `;
            } else {
                botonesHTML = `
                    <button class="btn btn-sm btn-outline-info me-1" onclick="abrirModalHorarios(${act.id}, '${act.tipoRegistro}')" title="Horarios">
                        <i class="bi bi-clock"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="abrirModalParticipantes(${act.id}, '${act.tipoRegistro}')" title="Participantes">
                        <i class="bi bi-people-fill"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-warning me-1" onclick="abrirModalEditarActividad(${act.id}, '${act.tipoRegistro}')" title="Editar">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="eliminarActividad(${act.id}, '${act.tipoRegistro}')" title="Desactivar">
                        <i class="bi bi-trash"></i>
                    </button>
                `;
            }

            tbody.innerHTML += `
                <tr class="${claseFila}"> 
                    <td class="align-middle">
                        <div class="text-primary fw-bold mb-1" style="font-size: 1.1rem;">
                            ${nombre} ${badgeTipo} 
                        </div>
                        <div class="text-muted small">
                            <i class="bi bi-person-badge"></i> Prof: ${respNombre}
                        </div>
                    </td>
                    <td class="text-success fw-bold align-middle">$${monto}</td>
                    <td class="align-middle">${badgeEstado}</td>
                    <td class="align-middle">
                        <div class="small mb-1"><i class="bi bi-geo-alt"></i> ${instNombre}</div>
                        <div>${htmlHorarios}</div>
                    </td>
                    <td class="align-middle text-end">
                        ${botonesHTML}
                    </td>
                </tr>
            `;
        });
    });
}

function abrirModalNuevaActividad() {
    document.getElementById('form-actividad').reset(); 
    cargarOpcionesInstalaciones('act-instalacion'); 
    cargarOpcionesResponsables('act-responsable'); 
    const modal = new bootstrap.Modal(document.getElementById('modalNuevaActividad'));
    modal.show();
}

function guardarNuevaActividad() {
    const tipoSeleccionado = document.getElementById('tipoActividad').value;
    const urlDestino = tipoSeleccionado === 'EVENTO' ? `${API_BASE_URL}/api/eventos` : `${API_BASE_URL}/api/actividades`;

    const datosNuevos = {   
        tipoSeleccionado: document.getElementById('tipoActividad').value, 
        nombre: document.getElementById('act-nombre').value,
        monto: parseFloat(document.getElementById('act-monto').value),
        instalacion: { id: parseInt(document.getElementById('act-instalacion').value) },
        responsable: { id: parseInt(document.getElementById('act-responsable').value) },
        estado: 'ACTIVO' 
    };

    fetch(urlDestino, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(datosNuevos)
    })
    .then(async respuesta => {
        if(respuesta.ok) {
            alert(`¡${tipoSeleccionado === 'EVENTO' ? 'Evento' : 'Actividad'} creado con éxito!`);
            location.reload(); 
        } else {
            const error = await respuesta.text();
            alert("Error al guardar: " + error);
        }
    })
    .catch(error => console.error('Error de conexión:', error));
}

function eliminarActividad(id, tipo) {
    const nombreTipo = tipo === 'EVENTO' ? 'evento' : 'actividad';
    if (confirm(`¿Estás seguro de que querés eliminar este ${nombreTipo}?`)) {
        const urlDestino = tipo === 'EVENTO' ? `${API_BASE_URL}/api/eventos/${id}` : `${API_BASE_URL}/api/actividades/${id}`;

        fetch(urlDestino, { method: 'DELETE' })
        .then(async respuesta => {
            if (respuesta.ok) {
                alert(`¡${nombreTipo.toUpperCase()} eliminado con éxito!`);
                cargarActividades(); 
            } else {
                const error = await respuesta.text();
                alert("Error al eliminar: " + error);
            }
        })
        .catch(error => console.error('Error:', error));
    }
}

function reactivarActividad(id, tipo) {
    const nombreTipo = tipo === 'EVENTO' ? 'evento' : 'actividad';
    if (confirm(`¿Querés volver a activar este ${nombreTipo}?`)) {
        const urlDestino = tipo === 'EVENTO' ? `${API_BASE_URL}/api/eventos/${id}/reactivar` : `${API_BASE_URL}/api/actividades/${id}/reactivar`;

        fetch(urlDestino, { method: 'PATCH' }) 
        .then(async respuesta => {
            if (respuesta.ok) {
                alert(`¡${nombreTipo.toUpperCase()} restaurado con éxito!`);
                cargarActividades();
            } else {
                const error = await respuesta.text();
                alert("Error al restaurar: " + error);
            }
        })
        .catch(error => console.error('Error:', error));
    }
}

function abrirModalEditarActividad(idActividad, tipo) {
    const actividad = actividadesArray.find(a => a.id === idActividad && (a.tipoRegistro === tipo));
    
    if (!actividad) {
        alert("No se encontraron los datos de la actividad.");
        return;
    }

    document.getElementById('editActividadId').value = actividad.id;
    document.getElementById('editNombreActividad').value = actividad.nombre;
    document.getElementById('editPrecioActividad').value = actividad.monto; 
    document.getElementById('editActividadId').dataset.tipo = tipo;
    
    if(actividad.instalacion) document.getElementById('editInstalacionActividad').value = actividad.instalacion.id;
    if(actividad.responsable) document.getElementById('editActividadId').dataset.responsable = actividad.responsable.id;

    modalEditarObj.show();
}

function guardarEdicionActividad() {
    modalEditarObj.hide();

    const id = document.getElementById('editActividadId').value;
    const responsableId = document.getElementById('editActividadId').dataset.responsable;
    const tipo = document.getElementById('editActividadId').dataset.tipo;
    
    const datosActualizados = {
        nombre: document.getElementById('editNombreActividad').value,
        monto: parseFloat(document.getElementById('editPrecioActividad').value),
        instalacion: { id: parseInt(document.getElementById('editInstalacionActividad').value) }
    };

    if (responsableId) datosActualizados.responsable = { id: parseInt(responsableId) };

    const urlDestino = tipo === 'EVENTO' ? `${API_BASE_URL}/api/eventos/${id}` : `${API_BASE_URL}/api/actividades/${id}`;

    fetch(urlDestino, {
        method: 'PUT', 
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(datosActualizados)
    })
    .then(async response => {
        if (!response.ok) {
            const mensajeBackend = await response.text();
            throw new Error(mensajeBackend || 'Error 400: Solicitud rechazada'); 
        }
        return response.json();
    })
    .then(data => {
        alert(`${tipo === 'EVENTO' ? 'Evento' : 'Actividad'} actualizada con éxito!`);
        cargarActividades(); 
    })
    .catch(error => alert("No se pudo guardar: " + error.message));
}


function abrirModalHorarios(idActividad) {
    document.getElementById('horario-actividad-id').value = idActividad;
    const contenedorLista = document.getElementById('listaHorariosContenido');
    contenedorLista.innerHTML = '<li class="list-group-item text-center text-muted">Cargando...</li>';
    modalHorariosObj.show();

    fetch(`${API_BASE_URL}/api/actividades/${idActividad}/horarios`)
        .then(response => {
            if (!response.ok) throw new Error('Error al cargar horarios');
            return response.json();
        })
        .then(horarios => {
            contenedorLista.innerHTML = ''; 
            if (horarios.length === 0) {
                contenedorLista.innerHTML = '<li class="list-group-item text-center text-muted">No hay horarios definidos.</li>';
                return;
            }

            const mapaDias = {
                "0": "Domingo", "1": "Lunes", "2": "Martes", "3": "Miércoles", "4": "Jueves", "5": "Viernes", "6": "Sábado", "7": "Domingo",
                "MONDAY": "Lunes", "TUESDAY": "Martes", "WEDNESDAY": "Miércoles", "THURSDAY": "Jueves", "FRIDAY": "Viernes", "SATURDAY": "Sábado", "SUNDAY": "Domingo",
                "LUNES": "Lunes", "MARTES": "Martes", "MIERCOLES": "Miércoles", "JUEVES": "Jueves", "VIERNES": "Viernes", "SABADO": "Sábado", "DOMINGO": "Domingo"
            };

            horarios.forEach(h => {
                const li = document.createElement('li');
                li.className = 'list-group-item d-flex justify-content-between align-items-center px-0 py-2 border-bottom';
                
                const valorDia = String(h.diaSemana).toUpperCase();
                const nombreDia = mapaDias[valorDia] || h.diaSemana;

                li.innerHTML = `
                    <div>
                        <span class="fw-bold text-dark me-2">${nombreDia}</span>
                        <span class="badge bg-light text-primary rounded-pill border">
                            ${h.horaInicio.substring(0,5)} - ${h.horaFin.substring(0,5)} hs
                        </span>
                    </div>
                    <button class="btn btn-sm btn-outline-danger border-0" onclick="eliminarHorario(${idActividad}, ${h.id})" title="Quitar horario">
                        <i class="bi bi-trash-fill"></i>
                    </button>
                `;
                contenedorLista.appendChild(li);
            });
        })
        .catch(error => {
            console.error('Error:', error);
            contenedorLista.innerHTML = '<li class="list-group-item text-center text-danger p-3"><i class="bi bi-exclamation-triangle"></i> No se pudieron cargar los horarios.</li>';
        });
}

function agregarHorario() {
    const actividadId = document.getElementById('horario-actividad-id').value;
    const nuevoHorario = {
        diaSemana: document.getElementById('horario-dia').value,
        horaInicio: document.getElementById('horario-inicio').value + ":00", 
        horaFin: document.getElementById('horario-fin').value + ":00"
    };

    if(!document.getElementById('horario-inicio').value || !document.getElementById('horario-fin').value) {
        alert("Por favor, ingresá la hora de inicio y de fin.");
        return;
    }

    if(nuevoHorario.horaInicio >= nuevoHorario.horaFin) {
        alert("La hora de inicio debe ser menor a la hora de fin."); 
        return;
    }

    fetch(`${API_BASE_URL}/api/actividades/${actividadId}/horarios`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(nuevoHorario)
    })
    .then(async respuesta => {
        if (respuesta.ok) {
            alert("¡Horario guardado con éxito!");
            document.getElementById('horario-inicio').value = '';
            document.getElementById('horario-fin').value = '';
            abrirModalHorarios(actividadId); 
            cargarActividades();
        } else if (respuesta.status === 400) {
            const mensajeError = await respuesta.text();
            alert("⚠️ " + mensajeError);
        } else {
            alert("Error interno al guardar el horario.");
        }
    })
    .catch(error => console.error('Error:', error));
}

function eliminarHorario(actividadId, horarioId) {
    if (confirm("¿Estás seguro de que querés quitar este horario?")) {
        fetch(`${API_BASE_URL}/api/actividades/${actividadId}/horarios/${horarioId}`, { method: 'DELETE' })
        .then(respuesta => {
            if (respuesta.ok) {
                abrirModalHorarios(actividadId);
                cargarActividades();
            } else {
                alert("Hubo un problema al intentar quitar el horario.");
            }
        })
        .catch(error => console.error('Error:', error));
    }
}


function abrirModalParticipantes(id, tipo) {
    document.getElementById('participantesActividadId').value = id;
    document.getElementById('participantesActividadTipo').value = tipo;
    
    const contenedorLista = document.getElementById('listaParticipantesContenido');
    const contador = document.getElementById('contadorParticipantes');
    const select = document.getElementById('selectNuevoParticipante');
    const label = document.getElementById('labelTipoInscripcion');
    
    label.innerText = tipo === 'EVENTO' ? 'Inscribir Persona (Abierto)' : 'Inscribir Socio (Exclusivo)';
    contenedorLista.innerHTML = '<li class="list-group-item text-center text-muted p-4"><div class="spinner-border spinner-border-sm text-primary me-2"></div>Cargando...</li>';
    select.innerHTML = '<option value="">Cargando...</option>';
    
    modalParticipantesObj.show();

    const urlRegistro = tipo === 'EVENTO' ? `${API_BASE_URL}/api/eventos/${id}` : `${API_BASE_URL}/api/actividades/${id}`;
    const urlCandidatos = tipo === 'EVENTO' ? `${API_BASE_URL}/api/personas` : `${API_BASE_URL}/api/socios`; 

    Promise.all([
        fetch(urlRegistro).then(r => r.ok ? r.json() : Promise.reject('Error en registro')),
        fetch(urlCandidatos).then(r => r.ok ? r.json() : Promise.reject('Error en candidatos'))
    ])
    .then(([registro, candidatos]) => {
        contenedorLista.innerHTML = '';
        select.innerHTML = '<option value="" selected disabled>Seleccione a quién inscribir...</option>';
        
        const participantes = registro.participantes || [];
        contador.textContent = participantes.length;

        if (participantes.length === 0) {
            contenedorLista.innerHTML = `
                <div class="text-center p-4 text-muted bg-light rounded-3 mt-2 border-dashed">
                    <i class="bi bi-person-x fs-2 d-block mb-2 text-secondary opacity-50"></i>
                    <span class="fw-bold">Nadie anotado todavía</span>
                </div>`;
        } else {
            participantes.forEach(p => {
                const nombre = p.persona ? p.persona.apynom : (p.apynom || `ID #${p.id}`);
                const dni = p.persona ? p.persona.dni : (p.dni || 'Sin DNI');
                
                contenedorLista.innerHTML += `
                    <li class="list-group-item d-flex justify-content-between align-items-center px-2 py-3 border-bottom">
                        <div class="d-flex align-items-center">
                            <div class="bg-primary bg-opacity-10 rounded-circle p-2 me-3 text-primary">
                                <i class="bi bi-person-fill fs-5"></i>
                            </div>
                            <div>
                                <h6 class="mb-0 fw-bold text-dark">${nombre}</h6>
                                <small class="text-muted">DNI: ${dni}</small>
                            </div>
                        </div>
                        <button class="btn btn-sm btn-outline-danger border-0" onclick="quitarParticipante(${id}, '${tipo}', ${p.id})" title="Desvincular">
                            <i class="bi bi-x-lg"></i>
                        </button>
                    </li>
                `;
            });
        }

        candidatos.forEach(c => {
            const nombreC = c.persona ? c.persona.apynom : (c.apynom || 'Sin nombre');
            const dniC = c.persona ? c.persona.dni : (c.dni || 'S/D');
            const yaInscrito = participantes.some(p => p.id === c.id);
            
            if (!yaInscrito) {
                select.innerHTML += `<option value="${c.id}">${nombreC} - DNI: ${dniC}</option>`;
            }
        });

        if (select.options.length === 1) {
            select.innerHTML = '<option value="" disabled>Todos están inscritos</option>';
            select.disabled = true;
        } else {
            select.disabled = false;
        }
    })
    .catch(error => {
        console.error('Error:', error);
        contenedorLista.innerHTML = '<li class="list-group-item text-danger text-center">Error al cargar datos.</li>';
    });
}

function inscribirParticipante() {
    const idActividadEvento = document.getElementById('participantesActividadId').value;
    const tipo = document.getElementById('participantesActividadTipo').value;
    const idCandidatoElegido = document.getElementById('selectNuevoParticipante').value;

    if (!idCandidatoElegido) {
        alert("Por favor, seleccioná a alguien de la lista.");
        return;
    }

    let urlDestino = tipo === 'EVENTO' 
        ? `${API_BASE_URL}/api/eventos/${idActividadEvento}/inscripcion-persona/${idCandidatoElegido}`
        : `${API_BASE_URL}/api/actividades/${idActividadEvento}/inscripcion-socio/${idCandidatoElegido}`;

    fetch(urlDestino, { method: 'POST' })
    .then(async respuesta => {
        if (respuesta.ok) abrirModalParticipantes(idActividadEvento, tipo);
        else {
            const error = await respuesta.text();
            alert("Error al inscribir: " + error);
        }
    })
    .catch(error => console.error('Error:', error));
}

function quitarParticipante(idRegistro, tipo, idParticipante) {
    if (!confirm('¿Estás seguro de que querés desvincular a esta persona?')) return;

    let urlDestino = tipo === 'EVENTO' 
        ? `${API_BASE_URL}/api/eventos/${idRegistro}/quitar-persona/${idParticipante}`
        : `${API_BASE_URL}/api/actividades/${idRegistro}/quitar-socio/${idParticipante}`;

    fetch(urlDestino, { method: 'DELETE' })
    .then(async respuesta => {
        if (respuesta.ok) abrirModalParticipantes(idRegistro, tipo);
        else {
            const error = await respuesta.text();
            alert("Error al desvincular: " + error);
        }
    })
    .catch(error => console.error('Error de red:', error));
}


function abrirModalPago(id, nombre, deuda) {
    document.getElementById('pagoPersonaId').value = id;
    document.getElementById('pagoNombrePersona').innerText = nombre;
    document.getElementById('pagoDeudaActual').innerText = deuda.toFixed(2);
    
    document.getElementById('pagoMonto').value = deuda.toFixed(2); 
    document.getElementById('pagoMonto').max = deuda; 
    
    modalPagoObj.show();
}

function procesarPago() {
    const id = document.getElementById('pagoPersonaId').value;
    const monto = parseFloat(document.getElementById('pagoMonto').value);
    const concepto = document.getElementById('pagoConcepto').value; 

    if (!monto || monto <= 0) {
        alert("El monto a pagar debe ser mayor a $0."); return;
    }
    if (!concepto.trim()) {
        alert("Por favor, ingresá un concepto para el pago (Ej: Cuota Abril)."); return;
    }

    fetch(`${API_BASE_URL}/api/personas/${id}/pagar?monto=${monto}&concepto=${encodeURIComponent(concepto)}`, {
        method: 'POST'
    })
    .then(async respuesta => {
        if (respuesta.ok) {
            alert("¡Cobro registrado en caja correctamente!");
            modalPagoObj.hide();
            document.getElementById('formPago').reset(); 
            cargarPersonas(); 
        } else {
            const error = await respuesta.text();
            alert("Error al procesar el pago: " + error);
        }
    })
    .catch(error => console.error('Error de red:', error));
}

function verHistorialPagos(idPersona, nombre) {
    document.getElementById('historialNombrePersona').innerText = `Socio/Persona: ${nombre}`;
    const lista = document.getElementById('listaHistorialPagos');
    lista.innerHTML = '<li class="list-group-item text-center text-muted py-4"><div class="spinner-border spinner-border-sm text-info"></div> Cargando...</li>';
    
    modalHistorialObj.show();

    fetch(`${API_BASE_URL}/api/personas/${idPersona}/pagos`)
        .then(res => res.json())
        .then(pagos => {
            lista.innerHTML = '';
            if (pagos.length === 0) {
                lista.innerHTML = '<li class="list-group-item text-center text-muted py-4"><i class="bi bi-inbox fs-3 d-block mb-2"></i>No hay pagos registrados.</li>';
                return;
            }

            pagos.forEach(pago => {
                const fecha = new Date(pago.fechaHora).toLocaleDateString('es-AR', {
                    year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute:'2-digit'
                });

                const conceptoMostrar = pago.concepto ? pago.concepto : 'Mercado Pago';

                lista.innerHTML += `
                    <li class="list-group-item d-flex justify-content-between align-items-center p-3">
                        <div>
                            <h6 class="mb-0 fw-bold text-dark">${conceptoMostrar}</h6>
                            <small class="text-muted"><i class="bi bi-calendar-event"></i> ${fecha}</small>
                        </div>
                        <div class="d-flex align-items-center gap-3">
                            <span class="badge bg-success bg-opacity-10 text-success border border-success rounded-pill px-3 py-2" style="font-size: 1rem;">
                                + $${pago.monto.toFixed(2)}
                            </span>
                            <a href="${API_BASE_URL}/api/pagos/${pago.id}/comprobante" class="btn btn-outline-danger btn-sm" target="_blank" title="Descargar Comprobante PDF">
                                <i class="bi bi-file-earmark-pdf-fill"></i> PDF
                            </a>
                        </div>
                    </li>
                `;
            });
        })
        .catch(error => {
            console.error("Error:", error);
            lista.innerHTML = '<li class="list-group-item text-danger text-center">Error al cargar el historial.</li>';
        });
}

function generarQR() {
    const id = document.getElementById('pagoPersonaId').value;
    const monto = parseFloat(document.getElementById('pagoMonto').value);
    const concepto = document.getElementById('pagoConcepto').value;

    if (!monto || monto <= 0 || !concepto.trim()) {
        alert("Asegurate de completar el concepto y un monto mayor a 0 antes de generar el QR.");
        return;
    }

    const btnQr = document.querySelector('button[onclick="generarQR()"]');
    btnQr.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Creando...';
    btnQr.disabled = true;

    fetch(`${API_BASE_URL}/api/personas/${id}/generar-qr?monto=${monto}&concepto=${encodeURIComponent(concepto)}`, {
        method: 'POST'
    })
    .then(async res => {
        if (!res.ok) {
            const errorTexto = await res.text();
            throw new Error(errorTexto); 
        }
        return res.json(); 
    })
    .then(data => {
        const modalFormulario = bootstrap.Modal.getInstance(document.getElementById('modalPago'));
        if(modalFormulario) {
            modalFormulario.hide();
        }

        document.getElementById('qrMontoMostrar').innerText = `Total: $${monto.toFixed(2)}`;
        document.getElementById('qrConceptoMostrar').innerText = concepto;

        document.getElementById('codigoQRGigante').classList.remove('d-none');
        document.getElementById('pagoExitosoAnimacion').classList.add('d-none');

        const divQR = document.getElementById('codigoQRGigante');
        divQR.innerHTML = ''; 

        new QRCode(divQR, {
            text: data.urlPago,
            width: 350,  
            height: 350, 
            colorDark : "#000000",
            colorLight : "#ffffff",
            correctLevel : QRCode.CorrectLevel.H
        });

        const modalQR = new bootstrap.Modal(document.getElementById('modalVistaQR'));
        modalQR.show();

        btnQr.innerHTML = '<i class="bi bi-qr-code"></i> Generar QR';
        btnQr.disabled = false;

        if (radarDePago) clearInterval(radarDePago);

        fetch(`${API_BASE_URL}/api/personas/${id}?t=${new Date().getTime()}`)
            .then(res => res.json())
            .then(socioInicial => {
                
                let deudaInicial = parseFloat(socioInicial.deuda);
                let deudaEsperada = deudaInicial - monto;
                
                if (deudaEsperada < 0) deudaEsperada = 0;

                console.log(`🎯 Deuda original: $${deudaInicial} | Esperamos que baje a: $${deudaEsperada}`);

                radarDePago = setInterval(() => {
                    fetch(`${API_BASE_URL}/api/personas/${id}?t=${new Date().getTime()}`)
                        .then(respuesta => respuesta.json())
                        .then(socioActual => {
                            
                            let deudaActual = parseFloat(socioActual.deuda);
                            console.log(`📡 Radar consultando... Deuda actual: $${deudaActual}`);

                            if (deudaActual <= (deudaEsperada + 0.01)) {
                                clearInterval(radarDePago); 
                                
                                document.getElementById('codigoQRGigante').classList.add('d-none');
                                document.getElementById('pagoExitosoAnimacion').classList.remove('d-none');
                                document.getElementById('qrMontoMostrar').innerText = ''; 
                                document.getElementById('qrConceptoMostrar').innerText = '';

                                cargarPersonas(); 
                            }
                        })
                        .catch(error => console.error("❌ Error en el radar:", error));
                }, 3000); 

            })
            .catch(error => console.error("❌ Error al obtener la deuda inicial:", error));
    });
}


let cacheActividades = [];

function toggleChat() {
    const chatWindow = document.getElementById('chatbot-window');
    chatWindow.classList.toggle('d-none');
    if (!chatWindow.classList.contains('d-none')) {
        document.getElementById('chat-input').focus();
    }
}

function mostrarBienvenidaConBotones() {
    const chatMessages = document.getElementById("chat-messages"); 
    const msgDiv = document.createElement("div");
    msgDiv.className = "d-flex justify-content-start mb-3 flex-column align-items-start";

    msgDiv.innerHTML = `
        <div class="d-flex flex-column gap-2 ms-2 mt-1 w-75" id="botones-rapidos">
            <button class="btn btn-sm btn-outline-dark rounded-pill text-start px-3 py-2 fw-semibold" onclick="procesarAccionBoton('morosos', this)">
                💸 Ver socios morosos
            </button>
            <button class="btn btn-sm btn-outline-dark rounded-pill text-start px-3 py-2 fw-semibold" onclick="procesarAccionBoton('actividades', this)">
                🎾 Ver actividades y eventos
            </button>
            <button class="btn btn-sm btn-outline-dark rounded-pill text-start px-3 py-2 fw-semibold" onclick="procesarAccionBoton('horarios', this)">
                🕒 Consultar horarios
            </button>
            <button class="btn btn-sm btn-outline-dark rounded-pill text-start px-3 py-2 fw-semibold" onclick="procesarAccionBoton('estado-inst', this)">
                🏟️ Estado de instalaciones
            </button>
            <button class="btn btn-sm btn-outline-dark rounded-pill text-start px-3 py-2 fw-semibold" onclick="procesarAccionBoton('stats-club', this)">
                📊 Estadísticas del club
            </button>
        </div>
    `;
    
    chatMessages.appendChild(msgDiv);

    setTimeout(() => {
        chatMessages.scrollTo({
            top: chatMessages.scrollHeight,
            behavior: 'smooth'
        });
    }, 10);
}

function procesarAccionBoton(accion, botonDOM) {
    const contenedor = botonDOM.parentElement;
    if (contenedor) contenedor.remove();

    if (accion === 'actividades') {
        agregarMensajeUI("¿Qué actividades y eventos hay disponibles?", 'usuario');
        consultarActividadesBD(); 
    } 
    else if (accion === 'horarios') {
        agregarMensajeUI("Quiero consultar horarios", 'usuario');
        cargarBotonesDeHorariosBD(); 
    }
    else if (accion === 'morosos') {
        agregarMensajeUI("Muéstrame la lista de socios morosos", 'usuario');
        consultarMorosos();
    }
    else if (accion === 'estado-inst') {
        agregarMensajeUI("¿Cómo están las instalaciones hoy?", 'usuario');
        consultarEstadoInstalaciones();
    }
    else if (accion === 'stats-club') {
        agregarMensajeUI("Quiero ver estadísticas del club", 'usuario');
        consultarEstadisticasClub();
    }
}

function consultarActividadesBD() {
    const idCarga = mostrarIndicadorEscribiendo();

    Promise.all([
        fetch(`${API_BASE_URL}/api/actividades`).then(res => res.ok ? res.json() : []),
        fetch(`${API_BASE_URL}/api/eventos`).then(res => res.ok ? res.json() : [])
    ])
    .then(([actividades, eventos]) => {
        quitarIndicadorEscribiendo(idCarga);
        
        const data = [...actividades, ...eventos];

        if (!data || data.length === 0) {
            agregarMensajeUI("Actualmente no hay actividades ni eventos cargados en la base de datos.", 'bot');
            return;
        }

        let textoRespuesta = "**Esto es lo que tenemos en el club:**\n\n";
        
        if (actividades.length > 0) {
            textoRespuesta += "**Actividades Regulares:**\n";
            actividades.forEach(act => {
                let precio = act.monto ? `$${act.monto}` : 'Gratis';
                textoRespuesta += `- **${act.nombre}** | Precio: ${precio}\n`;
            });
            textoRespuesta += "\n";
        }

        if (eventos.length > 0) {
            textoRespuesta += "**Eventos Especiales:**\n";
            eventos.forEach(ev => {
                let precio = ev.monto ? `$${ev.monto}` : 'Gratis';
                textoRespuesta += `- **${ev.nombre}** | Fecha: ${ev.fecha || 'A definir'} | Entrada: ${precio}\n`;
            });
        }

        agregarMensajeUI(textoRespuesta.trim(), 'bot');

        ofrecerSiguientePaso();
    })
    .catch(error => {
        console.error(error);
        quitarIndicadorEscribiendo(idCarga);
        agregarMensajeUI("⚠️ No me pude conectar con la base de datos de actividades.", 'bot');
    });
}

function cargarBotonesDeHorariosBD() {
    const idCarga = mostrarIndicadorEscribiendo();

    Promise.all([
        fetch(`${API_BASE_URL}/api/actividades`).then(res => res.ok ? res.json() : []),
        fetch(`${API_BASE_URL}/api/eventos`).then(res => res.ok ? res.json() : [])
    ])
    .then(([actividades, eventos]) => {
        quitarIndicadorEscribiendo(idCarga);
        
        cacheActividades = [...actividades, ...eventos]; 

        if (cacheActividades.length === 0) {
            agregarMensajeUI("No hay actividades ni eventos registrados en este momento.", 'bot');
            return;
        }
        
        agregarMensajeUI("Perfecto. ¿De qué actividad o evento necesitás saber los horarios/fechas?", 'bot');

        const chatMessages = document.getElementById("chat-messages");
        const btnDiv = document.createElement("div");
        btnDiv.className = "d-flex flex-wrap gap-2 ms-2 mb-3 mt-1 w-75";

        cacheActividades.forEach(item => {
            let colorBoton = item.fecha ? 'btn-outline-success' : 'btn-outline-primary';
            btnDiv.innerHTML += `<button class="btn btn-sm ${colorBoton} rounded-pill fw-semibold" onclick="responderHorarioDinamico(${item.id}, this, '${item.nombre}')">${item.nombre}</button>`;
        });

        chatMessages.appendChild(btnDiv);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    })
    .catch(error => {
        console.error(error);
        quitarIndicadorEscribiendo(idCarga);
        agregarMensajeUI("⚠️ Hubo un error al cargar los botones desde la base de datos.", 'bot');
    });
}

function consultarMorosos() {
    const idCarga = mostrarIndicadorEscribiendo();

    fetch(`${API_BASE_URL}/api/reportes/morosos`)
    .then(res => res.ok ? res.json() : Promise.reject('Error al cargar morosos'))
    .then(morosos => {
        quitarIndicadorEscribiendo(idCarga);
        
        if (morosos.length === 0) {
            agregarMensajeUI("¡Excelente noticia! No hay socios morosos en este momento. 🎉", 'bot');
            ofrecerSiguientePaso();
            return;
        }

        const chatMessages = document.getElementById('chat-messages');
        const msgDiv = document.createElement('div');
        msgDiv.className = 'mb-3 text-start';

        let htmlTarjetas = `
            <div class="badge bg-white text-dark border p-3 shadow-sm text-wrap text-start" style="font-size: 0.9rem; border-radius: 15px 15px 15px 0px; width: 85%;">
                <div class="mb-2 border-bottom pb-2">
                    <strong class="text-danger">⚠️ Atención:</strong> Se registraron ${morosos.length} deudas pendientes.
                </div>
        `;

        morosos.forEach(socio => {
            let nombre = socio.nombreCompleto || socio.apynom || "Sin nombre"; 
            let monto = socio.deudaTotal || socio.monto || 0;
            let dni = socio.dni || socio.documento || 'S/D';

            let deuda = parseFloat(monto);
            let montoMostrar = deuda > 0 ? `$${deuda.toLocaleString('es-AR')}` : 'Sin deuda';
            let colorBadge = deuda > 0 ? 'bg-danger bg-opacity-10 text-danger border-danger' : 'bg-success bg-opacity-10 text-success border-success';
            htmlTarjetas += `
                <div class="d-flex justify-content-between align-items-center mb-2 p-2 rounded" style="background-color: ${deuda > 0 ? '#ffe5e5' : '#e5ffe5'};">
                    <div>
                        <h6 class="mb-0 fw-bold">${nombre}</h6>
                        <small class="text-muted">DNI: ${dni || 'S/D'}</small>
                    </div>
                    <span class="badge ${colorBadge} rounded-pill px-3 py-2" style="font-size: 0.9rem;">${montoMostrar}</span>
                </div>
            `;
        });

        htmlTarjetas += '</div>';
        msgDiv.innerHTML = htmlTarjetas;
        chatMessages.appendChild(msgDiv);
        chatMessages.scrollTop = chatMessages.scrollHeight;
        ofrecerSiguientePaso();
    })
    .catch(error => {
        console.error(error);
        quitarIndicadorEscribiendo(idCarga);
        agregarMensajeUI("⚠️ No pude obtener la lista de socios morosos.", 'bot');
        ofrecerSiguientePaso();
    });
}

function consultarEstadoInstalaciones() {
    const idCarga = mostrarIndicadorEscribiendo();
    fetch(`${API_BASE_URL}/api/instalaciones/estado`)
    .then(res => res.ok ? res.json() : Promise.reject('Error al cargar estado de instalaciones'))
    .then(estado => {
        quitarIndicadorEscribiendo(idCarga);
        
        let respuesta = "**Estado actual de las instalaciones:**\n\n";
        estado.forEach(inst => {
            let estadoTexto = inst.estado === 'DISPONIBLE'
                ? `<span class="badge bg-success bg-opacity-10 text-success border border-success rounded-pill px-2 py-1">Disponible</span>`
                : `<span class="badge bg-danger bg-opacity-10 text-danger border border-danger rounded-pill px-2 py-1">No disponible</span>`;
            respuesta += `- **${inst.nombre}**: ${estadoTexto}\n`;
        });

        agregarMensajeUI(respuesta.trim(), 'bot');
        ofrecerSiguientePaso();
    })
    .catch(error => {
        console.error(error);
        quitarIndicadorEscribiendo(idCarga);
        agregarMensajeUI("⚠️ No pude obtener el estado de las instalaciones.", 'bot');
        ofrecerSiguientePaso();
    });
}

function responderHorarioDinamico(idBuscado, botonDOM, nombreAct) {
    botonDOM.parentElement.remove();
    agregarMensajeUI(`Horarios de ${nombreAct}`, 'usuario');

    const idCarga = mostrarIndicadorEscribiendo();

    setTimeout(() => {
        quitarIndicadorEscribiendo(idCarga);

        const actividad = cacheActividades.find(a => a.id === idBuscado);
        let respuesta = "";

        if (actividad.horarios && actividad.horarios.length > 0) {
            respuesta = `Los horarios registrados para **${actividad.nombre}** son:\n`;
            actividad.horarios.forEach(h => {
                let inicio = h.horaInicio ? h.horaInicio.substring(0, 5) : "";
                let fin = h.horaFin ? h.horaFin.substring(0, 5) : "";
                respuesta += `- **${h.diaSemana}** de ${inicio} a ${fin} hs.\n`;
            });
            
        } else if (actividad.fecha) {
            respuesta = `El evento **${actividad.nombre}** está programado para el día **${actividad.fecha}**.\nDuración estimada: ${actividad.duracion || 'a definir'} hs.`;
            
        } else {
            respuesta = `Por el momento, **${actividad.nombre}** no tiene horarios ni fechas definidos en el sistema.`;
        }

        agregarMensajeUI(respuesta, 'bot');

        ofrecerSiguientePaso();
    }, 800); 
}

function consultarEstadisticasClub() {
    const idCarga = mostrarIndicadorEscribiendo();

    const hoy = new Date();
    const primerDia = new Date(hoy.getFullYear(), hoy.getMonth(), 1).toISOString().split('T')[0];
    const ultimoDia = new Date(hoy.getFullYear(), hoy.getMonth() + 1, 0).toISOString().split('T')[0];

    Promise.all([
        fetch(`${API_BASE_URL}/api/reportes/estado-socios`).then(res => res.ok ? res.json() : {}),
        fetch(`${API_BASE_URL}/api/reportes/balance?inicio=${primerDia}&fin=${ultimoDia}`).then(res => res.ok ? res.json() : {}),
        fetch(`${API_BASE_URL}/api/reportes/actividades-inscriptos`).then(res => res.ok ? res.json() : {}),
        fetch(`${API_BASE_URL}/api/reportes/ocupacion-instalaciones`).then(res => res.ok ? res.json() : {}),
        fetch(`${API_BASE_URL}/api/reportes/recaudacion-por-tipo`).then(res => res.ok ? res.json() : {})
    ])
    .then(([socios, balance, actividades, instalaciones, recaudacion]) => {
        quitarIndicadorEscribiendo(idCarga);

        let totalSocios = 0, activos = 0;
        for (const [estado, cant] of Object.entries(socios)) {
            if (estado.toUpperCase().includes('ACTIVO') && !estado.toUpperCase().includes('IN')) activos = cant;
            totalSocios += cant;
        }

        let topActividad = Object.entries(actividades).sort((a, b) => b[1] - a[1])[0] || ['Ninguna', 0];
        let topInstalacion = Object.entries(instalaciones).sort((a, b) => b[1] - a[1])[0] || ['Ninguna', 0];

        const formatiarDinero = (num) => num ? '$' + num.toLocaleString('es-AR') : '$0';
        let ingresos = formatiarDinero(balance.totalIngresos);
        let gastos = formatiarDinero(balance.totalGastos);
        let saldo = formatiarDinero(balance.saldoFinal);

        const chatMessages = document.getElementById('chat-messages');
        const msgDiv = document.createElement('div');
        msgDiv.className = 'mb-3 text-start';

        msgDiv.innerHTML = `
            <div class="badge bg-white text-dark border p-3 shadow-sm text-wrap text-start" style="font-size: 0.85rem; border-radius: 15px 15px 15px 0px; width: 95%;">
                
                <div class="mb-3 border-bottom pb-2 d-flex align-items-center justify-content-between">
                    <div>
                        <span class="fs-5 me-1">📈</span>
                        <strong class="text-primary" style="font-size: 1rem;">Reporte Integral ClubSync</strong>
                    </div>
                    <span class="badge bg-light text-secondary border">Mes en curso</span>
                </div>

                <div class="row g-2 mb-3">
                    <div class="col-6">
                        <div class="card border-primary h-100 shadow-sm">
                            <div class="card-body p-2 text-center">
                                <h6 class="text-muted mb-1 text-uppercase" style="font-size: 0.65rem;">Padrón Socios</h6>
                                <h5 class="mb-0 text-primary fw-bold">${activos} <span class="text-muted fw-normal" style="font-size:0.75rem;">/ ${totalSocios} activos</span></h5>
                            </div>
                        </div>
                    </div>
                    <div class="col-6">
                        <div class="card border-success h-100 shadow-sm">
                            <div class="card-body p-2 text-center">
                                <h6 class="text-muted mb-1 text-uppercase" style="font-size: 0.65rem;">Saldo Mensual</h6>
                                <h5 class="mb-0 text-success fw-bold">${saldo}</h5>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="card bg-light border-0 shadow-sm mb-3">
                    <div class="card-body p-2 px-3 d-flex justify-content-between">
                        <div>
                            <small class="text-muted d-block" style="font-size: 0.7rem;">Total Ingresos</small>
                            <span class="text-success fw-bold">${ingresos}</span>
                        </div>
                        <div class="border-start mx-2"></div>
                        <div class="text-end">
                            <small class="text-muted d-block" style="font-size: 0.7rem;">Total Gastos</small>
                            <span class="text-danger fw-bold">-${gastos}</span>
                        </div>
                    </div>
                </div>

                <div class="mb-1 text-muted fw-bold" style="font-size: 0.75rem; letter-spacing: 0.5px;">MÉTRICAS DESTACADAS</div>
                
                <div class="card mb-1 shadow-sm w-100 border-0 border-start border-4 border-info">
                    <div class="card-body p-2 d-flex justify-content-between align-items-center">
                        <div class="text-truncate" style="max-width: 75%;">
                            <h6 class="mb-0 fw-bold text-dark text-truncate" style="font-size: 0.85rem;">🏆 Actividad Top</h6>
                            <span class="text-muted" style="font-size: 0.75rem;">${topActividad[0]}</span>
                        </div>
                        <span class="badge bg-info rounded-pill px-2 py-1 text-dark">${topActividad[1]} inscriptos</span>
                    </div>
                </div>

                <div class="card mb-1 shadow-sm w-100 border-0 border-start border-4 border-warning">
                    <div class="card-body p-2 d-flex justify-content-between align-items-center">
                        <div class="text-truncate" style="max-width: 75%;">
                            <h6 class="mb-0 fw-bold text-dark text-truncate" style="font-size: 0.85rem;">🏟️ Instalación más usada</h6>
                            <span class="text-muted" style="font-size: 0.75rem;">${topInstalacion[0]}</span>
                        </div>
                        <span class="badge bg-warning rounded-pill px-2 py-1 text-dark">${topInstalacion[1]} usos</span>
                    </div>
                </div>

            </div>
        `;

        chatMessages.appendChild(msgDiv);
        chatMessages.scrollTop = chatMessages.scrollHeight;

        ofrecerSiguientePaso();
    })
    .catch(error => {
        console.error("Error cargando el Súper Dashboard:", error);
        quitarIndicadorEscribiendo(idCarga);
        agregarMensajeUI("⚠️ Hubo un problema al recopilar los datos del club. Verificá la conexión con la base de datos.", 'bot');
        ofrecerSiguientePaso();
    });
}

function enviarMensaje() {
    const inputElement = document.getElementById("chat-input");
    const textoUsuario = inputElement.value.trim();
    if (textoUsuario === "") return;

    agregarMensajeUI(textoUsuario, 'usuario');
    inputElement.value = ""; 

    const idCarga = mostrarIndicadorEscribiendo(); 
    setTimeout(() => {
        quitarIndicadorEscribiendo(idCarga);
        let textoRespuesta = `Estás en la **versión de demostración guiada**. \n\nPor favor, utilizá los botones interactivos o cloná el proyecto localmente para habilitar la IA completa de lenguaje natural.`;
        agregarMensajeUI(textoRespuesta, 'bot');

        ofrecerSiguientePaso();
    }, 1000); 
}

function agregarMensajeUI(texto, emisor) {
    const chatMessages = document.getElementById('chat-messages');
    const msgDiv = document.createElement('div');
    const msgId = 'msg-' + Date.now() + '-' + Math.floor(Math.random() * 10000); 
    msgDiv.id = msgId;

    if (emisor === 'usuario') {
        msgDiv.className = 'mb-3 text-end';
        msgDiv.innerHTML = `<span class="badge bg-primary text-white p-3 shadow-sm text-wrap text-start" style="font-size: 0.9rem; border-radius: 15px 15px 0px 15px;">${marked.parse(texto)}</span>`;
    } else {
        msgDiv.className = 'mb-3 text-start';
        msgDiv.innerHTML = `<span class="badge bg-white text-dark border p-3 shadow-sm text-wrap text-start" style="font-size: 0.9rem; border-radius: 15px 15px 15px 0px;">${marked.parse(texto)}</span>`;
    }

    chatMessages.appendChild(msgDiv);
    chatMessages.scrollTop = chatMessages.scrollHeight; 
    return msgId;
}

function mostrarIndicadorEscribiendo() {
    const chatMessages = document.getElementById('chat-messages');
    const idCarga = 'typing-' + Date.now();
    const loadingDiv = document.createElement('div');
    loadingDiv.id = idCarga;
    loadingDiv.className = 'mb-3 text-start';
    loadingDiv.innerHTML = `<span class="badge bg-light text-secondary border p-2 shadow-sm text-wrap" style="border-radius: 15px 15px 15px 0px; font-size: 0.85rem;">Escribiendo... ✍️</span>`;
    chatMessages.appendChild(loadingDiv);
    chatMessages.scrollTop = chatMessages.scrollHeight;
    return idCarga;
}

function quitarIndicadorEscribiendo(id) {
    const loadingDiv = document.getElementById(id);
    if (loadingDiv) loadingDiv.remove();
}

function ofrecerSiguientePaso() {
    const chatMessages = document.getElementById("chat-messages");
    const div = document.createElement("div");
    
    div.className = "d-flex gap-2 justify-content-start mt-2 mb-4 ms-2";
    div.id = "flujo-cierre";
    
    div.innerHTML = `
        <button class="btn btn-xs btn-light border rounded-pill px-3 py-1 shadow-sm fw-medium" style="font-size: 0.8rem;" onclick="reiniciarFlujo(this)">
            🔄 Hacer otra consulta
        </button>
        <button class="btn btn-xs btn-light border rounded-pill px-3 py-1 shadow-sm fw-medium" style="font-size: 0.8rem;" onclick="cerrarConGracias(this)">
            🙏 Gracias, es todo
        </button>
    `;
    
    chatMessages.appendChild(div);

    chatMessages.scrollTo({
        top: chatMessages.scrollHeight,
        behavior: 'smooth'
    });
}

function reiniciarFlujo(boton) {
    boton.parentElement.remove();
    mostrarBienvenidaConBotones();

    const chatMessages = document.getElementById("chat-messages");
    setTimeout(() => {
        chatMessages.scrollTo({
            top: chatMessages.scrollHeight,
            behavior: 'smooth'
        });
    }, 10);
}

function cerrarConGracias(boton) {
    boton.parentElement.remove();
    agregarMensajeUI("¡De nada! Fue un gusto ayudarte. Si necesitás algo más, solo escribime. ¡Que disfrutes del club! 😊", 'bot');

    setTimeout(() => {
        mostrarBienvenidaConBotones();
    }, 2500);
}

cargarPersonas();
mostrarBienvenidaConBotones();