package org.mayocat.shop.store.datanucleus;

import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.xwiki.observation.ObservationManager;
import org.xwiki.test.annotation.MockingRequirement;

/**
 * Unit tests for the product store. Note: This tests is really about datanucleus persistence, so bean-validation
 * constraints are not tested here. They are tested both in the model module directly and in full-stack REST
 * integrations test.
 */
public class DNProductStoreTest extends AbstractStoreEntityTestCase
{
    @MockingRequirement(exceptions = {PersistenceManagerProvider.class, ObservationManager.class})
    private DNProductStore ps;

    @MockingRequirement(exceptions = {PersistenceManagerProvider.class})
    private DNTenantStore ts;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Tenant tenant;

    @Before
    public void registerTenant() throws Exception
    {
        this.tenant = this.ts.findByHandle("default");
        if (this.tenant == null) {
            this.ts.create(new Tenant("default"));
            this.tenant = this.ts.findByHandle("default");
        }
    }

    @Test
    public void testCreateProduct() throws Exception
    {
        Product p = new Product();
        p.setHandle("waterproof-fly-swatter");

        ps.create(p);

        Product p2 = ps.findByHandle("waterproof-fly-swatter");
        Assert.assertNotNull(p2);
    }

    @Test
    public void testCreateProductWithSameHandleButDifferentTenant() throws Exception
    {
        Product p = new Product();
        p.setHandle("leopard-fishnet-tights");

        ps.create(p);
        
        this.setTenantToResolveTo("other");
        
        Product p2 = new Product();
        p2.setHandle("leopard-fishnet-tights");;

        ps.create(p2);

        // No exception thrown -> OK
    }

    @Test
    public void testCreateProductThatAlreadyExistsForTenant() throws Exception
    {
        thrown.expect(EntityAlreadyExistsException.class);

        Product p = new Product();
        p.setHandle("peugeot-403-convertible");

        ps.create(p);

        Product p2 = new Product();
        p2.setHandle("peugeot-403-convertible");

        ps.create(p2);
    }

    @Test
    public void testUpdateProduct() throws Exception
    {
        Product p = new Product();
        p.setHandle("face-skin-mask");

        ps.create(p);
        ps.update(p);

        assertNotNull(ps.findByHandle("face-skin-mask"));
    }
}